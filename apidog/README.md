# Collection ClickTrack pour Apidog

Cette collection couvre les cinq endpoints du brief. Elle ne change pas le code de l'application et n'ajoute pas de bonus.

## Fichiers à importer

- `ClickTrack.postman_collection.json` : collection au format Postman v2.1, importable par Apidog.
- `ClickTrack.local.postman_environment.json` : environnement local, avec baseUrl égal à `http://localhost:8080/clicktrack`.

## Import dans Apidog

1. Utiliser l'import Postman d'Apidog et sélectionner les deux fichiers JSON.
2. Dans l'aperçu, conserver les dossiers, les scripts et les différents cas de requête. Plusieurs requêtes partagent le même endpoint : les conserver comme cas de test, plutôt que d'en remplacer une par une autre.
3. Pour cet import, conserver les URL complètes avec `{{baseUrl}}`. Ne pas ajouter une seconde fois `/clicktrack` dans le préfixe du serveur.
4. Sélectionner l'environnement `ClickTrack - Tomcat local` et adapter baseUrl si le port ou le contexte de Tomcat est différent. Ne pas utiliser un serveur mock.
5. Vérifier que les variables et les scripts après réponse sont présents. Les variables de collection Postman deviennent des variables de module dans Apidog.

Documentation de l'import : https://share.apidog.com/apidoc/docs-site/544525/import-from-postman-635043m0

## Exécution

Démarrer l'application dans Tomcat, puis envoyer les requêtes 01 à 11 dans l'ordre. On peut aussi construire un scénario de test Apidog avec ces étapes dans cet ordre, en incluant leurs scripts après réponse. La création d'un fichier importable ne réalise pas l'import dans l'application Apidog à ta place.

| Étape | Action | Résultat attendu |
| --- | --- | --- |
| 01 | Lister les publishers | 200 ; sélectionner automatiquement le premier publisher existant |
| 02 | Créer le premier lien | 201 ; mémoriser son id ; zéro clic |
| 03 | Ouvrir ce lien | 200 ; un clic |
| 04 | Ouvrir encore ce lien | 200 ; deux clics |
| 05 | Créer un second lien de la même offre | 201 ; mémoriser son id ; zéro clic |
| 06 | Ouvrir le second lien | 200 ; un clic |
| 07 | Lister les liens du publisher | Deux liens pour l'offre du scénario, avec compteurs 2 et 1 |
| 08 | Lister ses offres | Offre du scénario : nombreLiens 2 et clicks 3 |
| 09 | Ouvrir un lien absent | 404 |
| 10 | Créer un lien pour un publisher absent | 404 |
| 11 | Créer un lien avec une URL relative | 400 |

Les codes 400 et le choix du 404 pour publisher absent reflètent la validation de notre application ; le brief ne détaille pas tous ces cas d'erreur.

Chaque nouveau scénario doit commencer par 01. Cette étape choisit un nouveau nom d'offre à partir de l'heure et efface les anciens identifiants du scénario. Les anciens liens restent dans l'application, mais ne perturbent pas les contrôles de la nouvelle offre.

Ne pas exécuter plusieurs scénarios en parallèle dans le même module : ils partagent les variables. Ne pas ouvrir les liens de tracking à la main pendant le scénario : chaque ouverture compte un clic et change les résultats attendus. Une actualisation des listes n'ajoute aucun clic.

## Variables

| Variable | Rôle |
| --- | --- |
| baseUrl | Adresse du serveur, définie dans l'environnement local |
| publisherId | Premier publisher trouvé dans la réponse de 01 |
| publisherNom | Prénom et nom attendus dans la réponse de tracking |
| offre | Nom d'offre propre à cette exécution |
| lienId / secondLienId | Identifiants récupérés après les créations |
| clicksA / clicksB | Derniers compteurs reçus pour les deux liens |

Les identifiants ne sont pas fixés à 1 ou 2. Un publisher doit déjà exister ; l'initialisation actuelle en fournit trois. La collection n'ajoute aucun endpoint de création de publisher.

## Ce que vérifient les scripts

Les scripts `pm.test` contrôlent les statuts HTTP, les champs des réponses, les compteurs et le regroupement des offres. Ils enregistrent les identifiants avec `pm.collectionVariables.set` pour les requêtes suivantes. Ils ne testent pas la concurrence ; celle-ci est vérifiée par le test Java du projet.

Le tracking renvoie du JSON avec urlRedirection, publisher, offre et clicks. Aucune redirection HTTP n'est attendue.

## Validation du fichier

Les deux fichiers JSON et la syntaxe des douze scripts ont été vérifiés. La collection a ensuite été exécutée deux fois de suite avec Newman 6.2.1 sur le WAR déployé dans Tomcat 10.1.60 : 22 requêtes, 38 contrôles réussis, aucun échec. Le test commence avec des liens déjà présents et vérifie aussi la réexécution sans conflit avec les données anciennes.

Le rapport local est `target/apidog-newman-report.json`, ignoré par Git. Cette validation porte sur le fichier et les échanges HTTP. L'import et le rendu dans l'interface Apidog ne sont pas encore vérifiés.
