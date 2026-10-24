# ClickTrack

API Spring MVC pour créer des liens de tracking et suivre les clics par lien et par offre.

## Construire

Prérequis : Java 17+, Maven et Tomcat 10.1+ compatible avec Java 17.

```sh
mvn clean package
```

Cette commande compile, lance les tests et produit `target/clicktrack.war`. Si Maven n'est pas dans le terminal, utiliser les phases clean puis package depuis la fenêtre Maven d'IntelliJ.

## Lancer

1. Copier le WAR dans le dossier webapps de Tomcat.
2. Démarrer Tomcat avec `bin/startup.bat` sous Windows, ou depuis la configuration Tomcat d'IntelliJ.
3. Consulter `http://localhost:8080/clicktrack/api/publishers`.

Adapter le port et le contexte si le déploiement diffère de cet exemple. Le contexte `/clicktrack` correspond au nom de l'archive clicktrack.war.

Trois publishers fictifs sont créés au démarrage :

| id | Prénom et nom | Catégorie |
| --- | --- | --- |
| 1 | Alice Martin | INFLUENCEUR |
| 2 | Yanis Bernard | YOUTUBEUR |
| 3 | Sara Petit | BLOGUEUR |

Le stockage est en mémoire : redémarrer l'application efface les liens et clics, puis recrée les publishers de test.

## Routes

Chemins relatifs à `http://localhost:8080/clicktrack` :

| Méthode | Chemin | Résultat |
| --- | --- | --- |
| GET | `/api/publishers` | 200 : liste des publishers |
| POST | `/api/publishers/{id}/liens` | 201 : lien créé |
| GET | `/api/publishers/{id}/liens` | 200 : liens et compteurs du publisher |
| GET | `/api/publishers/{id}/offres` | 200 : offres, nombre de liens et somme des clics |
| GET | `/t/{id}` | 200 : informations après comptage ; 404 si lien absent |

Les données invalides et ids non positifs ou non numériques donnent 400. Un publisher absent donne 404. Un publisher existant sans lien reçoit un tableau vide pour ses liens et ses offres.

## Démonstration Postman

Une collection prête à importer dans Apidog et son environnement local sont disponibles dans [apidog/](apidog/README.md). Le guide indique l'ordre des requêtes et le paramétrage de l'URL Tomcat.

1. GET `/api/publishers` : repérer le publisher 1.
2. POST `/api/publishers/1/liens`, Content-Type `application/json`, avec :

```json
{
  "offre": "Formation Java",
  "urlDestination": "https://example.com/java"
}
```

La réponse contient l'id généré, l'offre, l'URL, publisherId 1 et clicks 0.

3. Ouvrir GET `/t/{id}` deux fois avec l'id renvoyé. La seconde réponse est :

```json
{
  "urlRedirection": "https://example.com/java",
  "publisher": "Alice Martin",
  "offre": "Formation Java",
  "clicks": 2
}
```

4. GET `/api/publishers/1/liens` : le lien affiche deux clics. Consulter les statistiques n'ajoute aucun clic.
5. Créer un second lien pour la même offre et l'ouvrir une fois.
6. GET `/api/publishers/1/offres` : nombreLiens 2 et clicks 3 pour cette offre.
7. Vérifier un tracking inexistant : 404. Envoyer `{}` au POST de création : 400.

La réponse de tracking reste en JSON. La redirection HTTP vers le site de destination est un bonus distinct non activé.

## Organisation

Le controller gère HTTP ; le service gère les règles métier ; le repository gère le stockage et les recherches. Les dépendances sont injectées par constructeur via des interfaces.

- `applicationContext.xml` : repositories, services et DataInitializer.
- `dispatcher-servlet.xml` : controllers et composants MVC.
- `web.xml` : démarrage Spring et DispatcherServlet.

Les beans sont déclarés en XML, sans annotations de stéréotype ni scan. Les annotations de routes restent utilisées. XmlRequestMappingHandlerMapping adapte la reconnaissance des controllers à Spring 6 en utilisant leur @RequestMapping, sans @Controller.

Les repositories stockent les données dans des ArrayList avec synchronisation des ajouts et parcours. Chaque lien utilise un AtomicInteger pour compter les clics sans perdre d'incrémentation concurrente. Les totaux des offres sont calculés à partir des liens du publisher.

## Vérification

- 17 tests Maven réussis : initialisation, cinq routes, validation, séparation des publishers, statistiques et erreurs.
- Huit threads et 2 000 appels au service de tracking : compteur final correct et résultats distincts de 1 à 2 000.
- Test réel du WAR sous Tomcat 10.1.60 : cinq routes, création 201, clics successifs, total de deux liens d'une même offre et erreurs 400/404.

MockMvc charge les XML réels. Le test concurrent porte sur le service et ne constitue pas un test de charge HTTP.

## Branches

`feature/` désigne les fonctionnalités ; `fix/` sera utilisé pour les corrections. Les branches reprennent les étapes précédemment validées.

La version XML complète et validée, avec sa documentation, est intégrée dans `config-xml`. Les branches `feature/` conservent les étapes de réalisation, notamment les trois user stories. La branche `main` n'a pas été modifiée lors de cette intégration.
