/*
 suppression d'un compte
*/

DELETE FROM UTILISATEUR WHERE Email = '';

/*
 création d'un compte
*/

INSERT INTO UTILISATEUR VALUES (Email = '', MotDePasse = '', Prenom = '', Nom = '', Adresse = '' , NULL);

/*
mise à jour les offres et les offres gagnantes
*/

UPDATE UTILISATEUR AS U
SET IDCompte = (SELECT IDCompte FROM OFFRE AS O WHERE O.IDCompte NOT IN (SELECT IDCompte FROM UTILSATEUR))
WHERE U.IDCompte = NULL
UNION
UPDATE UTILISATEUR AS U
SET IDCompte = (SELECT IDCompte FROM OFFRE_GAGNANTE AS OG WHERE OG.IDCompte NOT IN (SELECT IDCompte FROM UTILSATEUR))
WHERE U.IDCompte = NULL





