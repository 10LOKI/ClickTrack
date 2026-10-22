# ClickTrack - Préparation au débriefing

Ce document évolue à chaque tâche. Les questions ci-dessous sont des questions probables pour s'entraîner, pas une liste garantie des questions du jury.

## Notre méthode de travail

1. Réaliser une seule tâche et expliquer les changements.
2. Vérifier le résultat et présenter un checkpoint.
3. Attendre la validation de l'utilisateur avant tout commit et avant la tâche suivante.
4. Faire un commit par tâche validée. Publier sur GitHub uniquement sur demande.

## Parcours

- Tâche 1 : modèles - validée par l'utilisateur.
- Tâche 2 : repositories en mémoire et protection des accès simultanés.
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
- Statut : validé par l'utilisateur avec « on continue » ; prêt à être commité.
