import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class ApplicationUser {
		static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
		static final String USER = "elbouzia";
		static final String PASSWD = "elbouzia";

		private static void dumpResultSet(ResultSet rset) throws SQLException {
		    ResultSetMetaData rsetmd = rset.getMetaData();
		    int i = rsetmd.getColumnCount();
		    while (rset.next()) {
		        for (int j = 1; j <= i; j++) {
		            System.out.print(rset.getString(j) + "\t");
			    }
			    System.out.println();
		        }
		    }

		public static void main(String[] args){

			try {
				// Enregistrement du driver Oracle
				System.out.println("Loading Oracle thin driver...");
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				System.out.println("loaded.");

				// Etablissement de la connexion
				System.out.print("Connecting to the database... ");
				System.out.println("Connected");

				Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
        System.out.println("Checking connection ... :");
				System.out.println("Bonjour et bievenue dans l'application pour l'exploitation de la base de données !");
				Scanner scannerEntry = new Scanner(System.in);

		    String email ;
		    String passwd;
		    String idCompte;
				PreparedStatement stmt;
				ResultSet rset;

				System.out.println("S'inscrire OU Se connecter (1/2)");
				String u = String.valueOf(scannerEntry.nextLine());
				if ( u.equals("1")) {

					System.out.println("Veuillez entrer vote adresse email :");
					email = String.valueOf(scannerEntry.nextLine());
					System.out.println("Veuillez entrer votre mot de passe :");
					passwd = String.valueOf(scannerEntry.nextLine());
					System.out.println("Veuillez entrer votre Prénom :");
					String prenom = String.valueOf(scannerEntry.nextLine());
					System.out.println("Veuillez entrer votre Nom :");
					String nom = String.valueOf(scannerEntry.nextLine());
					System.out.println("Veuillez entrer votre Adresse :");
					String adresse = String.valueOf(scannerEntry.nextLine());
					System.out.println("Veuillez entrer un ID SVP :");
					int l;
					do{
						l = 0;
						idCompte = String.valueOf(scannerEntry.nextLine());
						stmt = conn.prepareStatement("SELECT IDCOMPTE FROM COMPTE WHERE IDCOMPTE = ?");
						stmt.setString(1, idCompte);
						rset = stmt.executeQuery();
						if (rset.next()) {
							System.out.println("Votre ID est deja utilise ! Veuillez réessayer");
							l = 1;
						}
					}while(l == 1);
					stmt = conn.prepareStatement("INSERT INTO COMPTE VALUES ( ? )");
					stmt.setString(1, idCompte);
					stmt.executeQuery();
					stmt = conn.prepareStatement("INSERT INTO UTILISATEUR VALUES ( ?,  ?,  ?, ?, ? , ?)");
					stmt.setString(1, email);
					stmt.setString(2, passwd);
					stmt.setString(3, prenom);
					stmt.setString(4, nom);
					stmt.setString(5, adresse);
					stmt.setString(6, idCompte);
					stmt.executeQuery();

				}else {
					System.out.println("Veuillez entrer vote adresse email :");
					email = String.valueOf(scannerEntry.nextLine());
					System.out.println("Veuillez entrer votre mot de passe :");
					passwd = String.valueOf(scannerEntry.nextLine());
				}
        // La requete de Test de connexion
				stmt = conn.prepareStatement("SELECT IDCOMPTE FROM utilisateur WHERE EMAIL = ? AND MOTDEPASSE = ?");
				stmt.setString(1, email);   // 1er parametre
				stmt.setString(2, passwd);  // 2eme parametre

        // Execution de la requete
				rset = stmt.executeQuery();

        // Resultat de la connexion
				ResultSetMetaData rsetmd = rset.getMetaData();
				if (!rset.next()) {
					System.out.println("Votre nom d'utilisateur ou mot de passe est incorrect ! Veuillez réessayer");
				}
				else{
					idCompte = rset.getString(1);
					System.out.println("Voulez-vous toutes les catégories (1), ou juste les catégories recommandées (2)? Entrez 1 ou 2 :");
					String type;
					do{
						type = String.valueOf(scannerEntry.nextLine());
						if (type.equals("1")){
							stmt = conn.prepareStatement("SELECT * FROM CATEGORIE");
							rset = stmt.executeQuery();
							dumpResultSet(rset);
						}else if(type.equals("2")){
									System.out.println("Vos recommandations selon vos offres précédentes :");
									stmt = conn.prepareStatement("SELECT COUNT(NOMC) AS NBR, NOMC FROM (SELECT P.IDPROD, P.NOMC FROM ((SELECT IDPROD,IDCOMPTE FROM OFFRE WHERE IDCOMPTE = ?)"
									+" MINUS (SELECT IDPROD,IDCOMPTE FROM OFFRE_GAGNANTE WHERE IDCOMPTE = ?)) M "
									+ "JOIN PRODUIT P ON P.IDPROD = M.IDPROD WHERE M.IDCOMPTE = ?) H "
									+ "JOIN OFFRE O ON O.IDPROD = H.IDPROD WHERE O.IDCOMPTE = ? "
									+ "GROUP BY NOMC ORDER BY NBR DESC");
									stmt.setString(1, idCompte);
									stmt.setString(2, idCompte);
									stmt.setString(3, idCompte);
									stmt.setString(4, idCompte);
									rset = stmt.executeQuery();
									dumpResultSet(rset);
									
									System.out.println("Vos recommandations selon les offres du moment :");
									stmt = conn.prepareStatement("SELECT SUM(NBR)/COUNT(*) AS FINAL_NBR,NOMC "
									+"FROM (SELECT COUNT(*) AS NBR, NOMC,IDPROD FROM (SELECT O.IDPROD, P.NOMC  FROM OFFRE O "
									+"JOIN PRODUIT P ON P.IDPROD = O.IDPROD) H "
									+"GROUP BY NOMC, IDPROD) M "
									+"GROUP BY M.NOMC ORDER BY FINAL_NBR DESC");
									rset = stmt.executeQuery();
									dumpResultSet(rset);
						}
									
								}while( !(type.equals("1") || type.equals("2")));
					String t;
					do {
						System.out.println("Veuillez entrer la catégorie du produit que vous désirez : ");
						String categorie = String.valueOf(scannerEntry.nextLine());
						
						//Affichage des produits qui sont toujours disponible
						stmt = conn.prepareStatement("SELECT H.IDPROD, H.INTITULE, H.PRIXCOURANT, H.URL, H.NOMC FROM "
						+ "(SELECT COUNT(*) AS NBROFFRE,P.IDPROD, P.INTITULE, P.PRIXCOURANT, P.URL, P.NOMC  FROM PRODUIT P "
						+ "LEFT JOIN OFFRE O ON O.IDPROD = P.IDPROD "
						+ "WHERE NOMC IN ((SELECT * FROM CATEGORIE WHERE NomC = ?) UNION "
						+ "(SELECT A.NomC FROM CATEGORIE C JOIN A_PARENT A ON C.NomC = A.NomParent WHERE C.NomC = ?)) "
						+ "AND P.IDPROD NOT IN (SELECT IDPROD FROM OFFRE_GAGNANTE) "
						+ "GROUP BY P.IDPROD, P.INTITULE, P.PRIXCOURANT, P.URL, P.NOMC "
						+ "ORDER BY NBROFFRE DESC) H");

						stmt.setString(1, categorie);
						stmt.setString(2, categorie);
						rset = stmt.executeQuery();
						dumpResultSet(rset);
						System.out.println("Voulez-vous faire une offre sur un produit (attention le prix proposé doit être supérieur au prix du produit) ? Entrez 1 pour oui, 0 pour non : ");
						if (scannerEntry.nextLine().equals("1")) {
							System.out.println("Entrez l'ID du produit pour lequel vous voulez faire une offre :");
							String produit = String.valueOf(scannerEntry.nextLine());
							System.out.println("Entrez le prix que vous souhaitez proposer :");
							int prixPropose = Integer.valueOf(scannerEntry.nextLine());
							
							stmt = conn.prepareStatement("SELECT prixcourant FROM Produit WHERE IDProd = ?");
							stmt.setString(1, produit);
							rset = stmt.executeQuery();
							rset.next();
							int prixCourant = rset.getInt(1);
							
							
							//Début de la transaction pour effectuer une offre
							conn.setAutoCommit(false);
							conn.setTransactionIsolation(conn.TRANSACTION_SERIALIZABLE);

							stmt = conn.prepareStatement("INSERT INTO OFFRE VALUES (?, to_date(?, 'YYYY-MM-DD HH24:MI:SS'), ?, ?)");
							stmt.setString(1, produit);
							String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
							stmt.setString(2, currentDate);
							stmt.setInt(3, prixPropose);
							stmt.setString(4, idCompte);
							stmt.executeQuery();
							
							//Si le prix propose est inférieur au prix courant l'offre est supprimée
							stmt = conn.prepareStatement
							("DELETE FROM OFFRE WHERE IDCompte = ? and PrixPropose <= ? and Heure = to_date(?, 'YYYY-MM-DD HH24:MI:SS')");
							stmt.setString(1, idCompte);
							stmt.setInt(2, prixCourant);
							stmt.setString(3, currentDate);
							stmt.executeQuery();

							stmt = conn.prepareStatement
							("UPDATE PRODUIT SET PrixCourant = ? WHERE ? > PRIXCOURANT AND IdProd = ? AND IdProd NOT IN(SELECT IDPROD FROM OFFRE_GAGNANTE)");
							stmt.setInt(1, prixPropose);
							stmt.setInt(2, prixPropose);
							stmt.setString(3, produit);
							stmt.executeQuery();
							
							stmt = conn.prepareStatement
							("Select count(heure) from offre where idprod = ?");
							stmt.setString(1, produit);
							rset = stmt.executeQuery();
							rset.next();
							int numOffre = rset.getInt(1);
							if (numOffre == 5) {
								System.out.println("Félicitations vous avez remporté l'enchère pour ce produit !");
								stmt = conn.prepareStatement("INSERT INTO OFFRE_GAGNANTE VALUES(?,to_date(?, 'YYYY-MM-DD HH24:MI:SS'),?)");
								stmt.setString(1, produit);
								stmt.setString(2, currentDate);
								stmt.setString(3, idCompte);
								stmt.executeQuery();
								conn.commit();
							}
							else if (numOffre < 5) {
								conn.commit();
								System.out.println("Vous n'avez pas encore gagné cette enchère, mais vous avez la possibilité de faire une autre offre !");
								
							}
							else if (numOffre > 5){
								conn.commit();
								System.out.println("Ce produit n'est plus disponible à la vente.");
							}
							
						}
						System.out.println("Acheter un autre produit ? (O/N)");
						t = String.valueOf(scannerEntry.nextLine());
						}while( t.equals("O"));

					System.out.println("Voulez-vous vous deconnecter ? Ou supprimer votre Compte? (1/2)");
					String supp = String.valueOf(scannerEntry.nextLine());
					if ( supp.equals("2")){
						 conn.setAutoCommit(false);
						 conn.setTransactionIsolation(conn.TRANSACTION_SERIALIZABLE);
						 stmt = conn.prepareStatement("DELETE FROM UTILISATEUR WHERE Email = ?");
						 stmt.setString(1, email);
						 stmt.executeQuery();
						 System.out.println("La suppresion de votre compte a réussi ! On espere vous revoir le plus tôt possible");
					} else if (supp.equals("1")){
						System.out.println("Au revoir cher client !");
					}
				}
        // Fermeture
      	rset.close();
      	stmt.close();
      	conn.close();
        } catch (SQLException e) {
            System.err.println("failed");
            e.printStackTrace(System.err);
        }
			}
}
