import java.sql.*;
import java.util.Scanner;

public class ApplicationBD {
	    public static void main(String[] args){
    	    String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
	    	
    	    System.out.println("Bonjour et bievenue dans l'application pour l'exploitation de la base de données !"+"\n"+ "Entrez 1 pour commencer vos requêtes SQL : ");
	        Scanner scannerEntry = new Scanner(System.in);
	        String game = scannerEntry.nextLine();
	            if (game.equals("1")){
	                String userID ;
	                String passwd;
	                System.out.println("Veuillez entrer votre nom d'utilisateur :");
	                userID = String.valueOf(scannerEntry.nextLine());   
	                
	                System.out.println("Veuillez entrer votre mot de passe :");
	                passwd = String.valueOf(scannerEntry.nextLine());   
	               
	                try {
	                // Enregistrement du driver Oracle
	                System.out.println("Loading Oracle thin driver...");
	                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	                System.out.println("loaded.");
	                
	             // Etablissement de la connection
	        	    System.out.print("Connecting to the database... "); 
	                System.out.println("Connected");
	                } catch (SQLException e) {
	                    System.err.println("failed");
	                    e.printStackTrace(System.err);
	                }
	                
	                do {
	                try {
	                Connection conn = DriverManager.getConnection(CONN_URL, userID, passwd);
	                System.out.println("Veuillez entrer la requête que vous souhaitez appliquer :");
	                String requete;
	                requete = String.valueOf(scannerEntry.nextLine());
	                
	             // Creation de la requete
	                PreparedStatement stmt = conn.prepareStatement(requete);
	    	    // Execution de la requete
	                ResultSet rset = stmt.executeQuery();
	    	    // Affichage du resultat
	                System.out.println("Results:");
	                ResultSetMetaData rsetmd = rset.getMetaData();
	                int i = rsetmd.getColumnCount();
	                while (rset.next()) {
	                    for (int j = 1; j <= i; j++) {
	                        System.out.print(rset.getString(j) + "\t");
	        	    }
	        	    System.out.println();
	                }
	                System.out.println("");
	             // Fermeture 
		            rset.close();
		            stmt.close();
		            conn.close();
	                } catch (SQLException e) {
	                    System.err.println("failed");
	                    e.printStackTrace(System.err);
	                    continue;
	                }
	                }while(true);
	                

	            }
	            else {
	            	System.out.println("Dommage ! Si vous changez d'avis, n'hésitez pas à réexecuter l'application ;)");
	            	}
	    }
}
