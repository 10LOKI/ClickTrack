# ClickTrack - Préparation au débriefing

Ce document évolue à chaque tâche. Les questions ci-dessous sont des questions probables pour s'entraîner, pas une liste garantie des questions du jury.

## Notre méthode de travail

1. Réaliser une seule tâche et expliquer les changements.
2. Vérifier le résultat et présenter un checkpoint.
3. Attendre la validation de l'utilisateur avant tout commit et avant la tâche suivante.
4. Faire un commit par tâche validée. Publier sur GitHub uniquement sur demande.

## Parcours

- Tâche 1 : modèles - validée par l'utilisateur.
- Tâche 2 : repositories en mémoire et protection des accès simultanés - validée par l'utilisateur.
- Tâche 3 : services et objets de requête/réponse.
- Tâche 4 : cinq endpoints du brief.
- Tâche 5 : configuration XML et données initiales.
- Tâche 6 : vérification fonctionnelle et concurrence.
- Bonus : à décider après le socle obligatoire.

## Tâche 1 - Modèles

### Rôle des classes

- `Publisher` représente un éditeur : id, nom, prénom, email et catégorie.
- `Lien` représente un lien de tracking : id, offre, URL de destination, nombre de clics et identifiant du publisher.
- `Categorie` est une enum : INFLUENCEUR, YOUTUBEUR, BLOGUEUR. Ces valeurs existent dans le projet ; le brief impose une enum sans préciser ses valeurs.
- L'offre reste une chaîne dans `Lien`, conformément aux entités du brief. Son total de clics sera calculé plus tard à partir des liens.

### Concepts et questions probables

**Qu'est-ce que l'encapsulation ?**
Les champs sont privés et accessibles par des méthodes publiques. Cela permet de contrôler les opérations : le compteur de clics se modifie par `incrementerClicks()`, sans setter permettant de lui attribuer une valeur arbitraire.

**Pourquoi utiliser `Long` au lieu de `long` pour l'id ?**
`long` est un type primitif, `Long` est un objet qui peut aussi valoir `null`. Avant son enregistrement, un publisher peut ne pas avoir d'identifiant. Le champ et son getter utilisent maintenant tous les deux `Long`.

**Quel était le problème dans le code initial ?**
Le getter retournait `long`, alors que le repository faisait `p.getId().equals(id)`. Un primitif n'a pas de méthode `.equals()`, donc ce code ne compilait pas. Le retour `Long` corrige ce problème. Il évite aussi le déballage automatique d'un identifiant null, qui aurait provoqué une NullPointerException.

**Quelle différence entre `==` et `.equals()` ?**
Pour les primitifs, `==` compare les valeurs. Pour les objets, `==` compare les références ; `.equals()` compare selon la définition de la classe, par exemple la valeur numérique pour `Long`. Appeler `.equals()` sur une référence null provoque une exception : la correction du type ne supprime pas ce risque général.

**Pourquoi une enum pour la catégorie ?**
Elle limite les valeurs possibles et évite les catégories libres avec des fautes de frappe. Elle exprime ce choix dans le type Java.

**Pourquoi deux constructeurs ?**
Le constructeur sans argument permet de créer un objet puis de renseigner ses champs. Le constructeur avec paramètres initialise plusieurs champs dès la création. La validation des données reste à traiter dans les prochaines tâches.

**Pourquoi renommer `urlDest` en `urlDestination` ?**
Pour reprendre le vocabulaire du brief. Le champ, le paramètre du constructeur, le getter et le setter utilisent ce même nom. Aucun autre code existant n'utilisait les anciennes méthodes.

**Pourquoi un `AtomicInteger` pour les clics ?**
Un simple `clicks++` lit, calcule puis écrit. Deux threads peuvent lire la même ancienne valeur et perdre une incrémentation. `incrementAndGet()` effectue une incrémentation atomique et retourne le résultat. Le compteur existant est conservé.

**Que signifie `final` sur le compteur ?**
La référence du compteur ne peut pas être remplacée après son initialisation. Sa valeur interne peut toujours changer : `final` ne rend pas l'objet immuable.

**Le compteur atomique suffit-il pour toute la concurrence ?**
Non. Il protège l'incrémentation d'un compteur. Les ajouts et parcours des listes des repositories doivent aussi être protégés. Cette partie sera traitée séparément.

**Pourquoi `publisherId` plutôt qu'un objet Publisher dans Lien ?**
Le brief prévoit un identifiant de publisher dans le lien. Le repository permettra de retrouver le publisher correspondant ; l'existence de ce publisher sera vérifiée dans la couche service.

### Vérifications effectuées

- Compilation avec Java 17 des trois modèles et du repository Publisher existant : réussie.
- Vérification par un petit programme temporaire : id initial null, accès aux champs du publisher, enregistrement et recherche par id, champs du lien, modification de l'URL et passage du compteur de 0 à 1 : réussie.
- Maven n'est pas accessible par la commande `mvn` dans le terminal de cette session. La compilation Maven complète et le test de concurrence ne sont pas validés à ce checkpoint.

### Checkpoint 1

- Modifications : retour `Long` de `Publisher.getId()` et nom `urlDestination` dans `Lien`.
- `Categorie` a été vérifiée sans modification.
- Statut : validé par l'utilisateur avec « on continue » ; commit `0036a04` créé.

## Tâche 2 - Repositories en mémoire

### Changements

- Création de `LienRepository` : `save`, `findById` et `findByPublisherId`.
- Création de `LienRepositoryImpl` : données conservées dans une ArrayList enveloppée par `Collections.synchronizedList`.
- Dans les deux repositories, attribution de l'id et ajout dans la liste regroupés dans un bloc `synchronized`.
- Suppression d'un import Swing inutilisé dans `PublisherRepositoryImpl`.
- Aucune annotation de stéréotype : les beans seront déclarés dans la configuration XML à la tâche 5.

### Concepts et questions probables

**Quel est le rôle d'un repository ?**
Il gère le stockage et la recherche des données. Les règles métier et les réponses HTTP appartiennent aux couches service et controller.

**Pourquoi une interface et une implémentation ?**
L'interface décrit les opérations disponibles ; l'implémentation fournit le stockage en mémoire. Les services pourront dépendre de l'interface par injection, comme demandé dans le brief.

**Pourquoi une ArrayList ?**
Le brief impose ce stockage en mémoire. Les données disparaissent au redémarrage. Il n'y a pas de base de données à cette étape.

**Pourquoi `synchronizedList` et des blocs `synchronized` ?**
L'enveloppe protège les opérations individuelles comme `add`. Un parcours, un stream ou une copie parcourt plusieurs éléments : il faut garder le verrou de la liste pendant toute l'opération. Le bloc autour de `save` regroupe aussi l'attribution d'id et l'insertion vis-à-vis des autres opérations du repository.

**Sur quel objet se trouve le verrou ?**
Sur la liste du repository concerné : `publishers` ou `liens`. Les lectures et écritures d'un même repository utilisent le même verrou. Les deux repositories ont des verrous distincts.

**Pourquoi `AtomicLong` pour les identifiants ?**
`incrementAndGet()` fournit une séquence atomique propre à chaque repository. Ici, il est conservé depuis le repository Publisher et repris pour les liens ; un compteur long protégé par le même verrou serait aussi suffisant. Ce n'est pas le compteur des clics : celui-ci est un AtomicInteger dans chaque lien.

**Pourquoi retourner une copie de la liste ?**
L'appelant peut vider ou modifier la liste reçue sans retirer d'objets du stockage. C'est une copie superficielle : les objets contenus restent partagés. Leurs setters ne sont pas rendus sûrs pour la concurrence par la copie. Le compteur de clics possède sa propre protection atomique.

**Pourquoi `Optional` pour une recherche par id ?**
Il exprime explicitement qu'un résultat peut être absent. Le service décidera ensuite comment traiter cette absence ; le repository ne décide pas d'un statut HTTP.

**Pourquoi filtrer les liens par publisher ?**
Pour lister uniquement les liens de l'éditeur demandé. Cette même liste permettra au service de regrouper les clics par offre. La recherche seule ne constitue pas un système d'authentification.

**Que fait `save` dans ce projet ?**
Il ajoute un nouvel objet et lui attribue un id généré, même si un id a été fourni auparavant. La mise à jour n'est pas implémentée. On doit lui transmettre un nouvel objet par création, sans réenregistrer la même instance.

### Vérifications effectuées

Compilation des modèles et de tous les repositories avec Java 17, puis exécution d'un programme temporaire de vérification :

- Recherche d'un id absent et recherche d'un publisher enregistré : réussies.
- Filtrage des liens entre deux publishers : réussi.
- Modification des listes retournées sans perte dans le stockage : réussie.
- Huit threads ont créé au total 4 000 publishers et 4 000 liens supplémentaires, avec des lectures pendant les ajouts : aucune exception, aucun ajout perdu et aucun id dupliqué dans les populations vérifiées.
- Les mêmes threads ont incrémenté un lien partagé 4 000 fois : compteur final égal à 4 000.
- `git diff --check` : aucune erreur d'espacement.

Ce contrôle ciblé ne remplace pas les futurs tests Maven et HTTP de l'application complète.

### Checkpoint 2

- Statut : validé par l'utilisateur ; commit et publication autorisés.
- Prochaine tâche après validation et commit : services et objets de requête/réponse.

## Organisation Git et user stories

### Branches préparées

| Branche | Objectif | Critères à vérifier lors de son implémentation |
| --- | --- | --- |
| `config-xml` | Branche existante de départ ; configuration XML retenue pour le projet | Déclaration explicite des beans, injection via interfaces, démarrage Spring MVC |
| `codex/socle-repositories` | Socle partagé : modèles et repositories des tâches 1 et 2 | Compilation Java 17, stockage en ArrayList, ajouts et recherches protégés |
| `codex/us1-creer-lien` | En tant que publisher, créer un lien vers une URL de destination rattaché à une offre | POST `/api/publishers/{id}/liens`, réponse 201, lien associé au publisher ; traitement des données invalides et du publisher absent à préciser dans les prochaines tâches |
| `codex/us2-lister-liens` | En tant que publisher, lister mes liens avec leur nombre de clics | GET `/api/publishers/{id}/liens`, réponse 200, uniquement les liens du publisher et leurs compteurs |
| `codex/us3-lister-offres` | En tant que publisher, lister mes offres avec leur nombre de clics | GET `/api/publishers/{id}/offres`, réponse 200, regroupement par offre et somme des clics des liens du publisher |

Les trois branches US partent du même commit de socle. Leur existence ne signifie pas que les fonctionnalités sont implémentées. Les services et controllers sont encore à réaliser. La branche de travail suivante est `codex/us1-creer-lien`.

Le brief demande aussi GET `/api/publishers` et GET `/t/{id}` : ce sont des exigences complémentaires à intégrer aux prochains checkpoints, sans les présenter comme des user stories supplémentaires du document. Le tracking doit incrémenter le compteur puis afficher les informations demandées ; la redirection est un bonus.

### Ce qu'il faut savoir expliquer au débriefing

**Pourquoi une branche de socle ?**
Les modèles et repositories servent aux trois user stories. Leur travail commun est isolé dans une branche de base au lieu d'être dupliqué dans chaque fonctionnalité.

**Quelle différence entre une tâche technique et une user story ?**
Une user story décrit un besoin utilisateur et sa valeur. Une tâche technique, comme protéger une ArrayList ou configurer un bean, contribue à satisfaire ce besoin. Une user story peut nécessiter plusieurs couches et plusieurs checkpoints.

**Que représentent `config-xml`, `config-java` et `config-annotation` ?**
Ce sont les modes de configuration décrits dans le brief, pas des user stories métier. Pour l'instant, nous travaillons avec XML et ne créons pas d'autres implémentations de configuration.

**Quelle différence entre commit et push ?**
Un commit enregistre un état local avec un message. Un push publie les commits d'une branche sur GitHub. La validation pédagogique du checkpoint reste nécessaire même si le code compile.

**Qu'est-ce qu'une branche et sa branche de suivi ?**
Une branche désigne un commit et avance avec les commits suivants. Plusieurs branches peuvent initialement pointer vers le même commit. La branche de suivi, par exemple `origin/codex/us1-creer-lien`, sert à comparer la branche locale à sa référence distante.

**Comment intégrer les user stories plus tard ?**
On intégrera les branches validées dans la branche de configuration choisie. Une pull request permet de relire les différences avant fusion. Une branche US créée depuis le socle n'inclut pas automatiquement les changements futurs des autres branches : il faudra les intégrer explicitement si une dépendance apparaît.

**Que présenter pour chaque user story ?**
Le besoin, les critères attendus, les classes modifiées, le chemin controller → service → repository, un exemple de requête/réponse, les cas d'erreur traités et les résultats des vérifications. Distinguer les exigences du brief des choix techniques et des bonus.

### Traçabilité des commits et publication

- Tâche 1 : commit `0036a04`, modèles et première version de ce document.
- Tâche 2 : commit dédié au socle et à cette documentation ; son identifiant se consulte dans l'historique Git.
- Publication demandée par l'utilisateur : branche de socle et trois branches US, sans fusion dans `main` et sans push forcé.
- La réussite du push sera contrôlée sur GitHub par la correspondance des identifiants des branches locales et distantes.
