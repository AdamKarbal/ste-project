# SopiaTech Eats-Team-25-26

## TEAM O

PO : BOULET Olivier (FISA)\
SA : GUERRAOUI Ismail (FISE)\
QA : MENOU Clement (FISE)\
OPS : KARBAL Adam (FISE)\
QA & PO : MADANDAR ARANI Ali (FISE)

## doc

-   [Rendue TD1](doc/RenduTD1.pdf)
-   [Rendue D0](doc/D0.pdf)
-   [Rendue D1](doc/TeamO-renduD1.pdf)

## Stratégie de gestion des branches

Nous appliquons la stratégie suivante pour le développement :

-   **Branche `main`** : Cette branche contient une version "presque
    stable" du projet. Nous évitons de travailler directement dessus pour les grosses features.
    Pour y intégrer du code, nous effectuons des **Pull Requests**. Il
    est possible que des instabilités mineures surviennent après une PR.
-   **Branches de travail** : Le développement s'effectue sur des
    branches dédiées (features, etc.). C'est sur ces branches que
    se trouvent les versions instables en cours de construction avant
    d'être proposées en Pull Request.

## Lancer le projet et l'installer

### Prérequis

Avant de commencer, assurez-vous d'avoir installé : - Java 21 (ou
version compatible) - Maven 3.9+ - Node.js et Angular CLI (pour la
partie Frontend) - Un environnement de développement compatible
(IntelliJ IDEA, VS Code, Eclipse, etc.)

------------------------------------------------------------------------

### Compilation et exécution des tests

Le projet utilise Maven pour compiler le code, gérer les dépendances et
exécuter automatiquement les tests unitaires et fonctionnels (JUnit5 +
Cucumber).

Pour lancer une compilation complète et vérifier que tout fonctionne :

``` bash
mvn clean package
```

------------------------------------------------------------------------

## Lancement de l'application complète

L'application est composée d'un Backend et
d'un Frontend. Voici la procédure pour lancer l'environnement complet.

### 1. Lancement du Backend (APIs)

1.  Ouvrez le projet dans votre IDE.\
2.  Naviguez vers la classe :\
    `src/main/java/Api/Utils/ServersStarter.java`\
3.  Exécutez la méthode `main` de cette classe.

Cela va démarrer les services suivants :

-   AccountRepoServer\
-   CustomerServer\
-   DishAIServer\
-   DishRepoServer\
-   MockAIServer\
-   OrderServer\
-   OrderRepoServer\
-   RestaurantServer\
-   RestaurantRepoServer\
-   RestaurantScheduleServer

Attendez que le message `Servers started successfully` s'affiche dans la
console.

### 2. Lancement du Frontend

Une fois le backend démarré :

1.  Ouvrez un terminal à la racine du projet.\
2.  Déplacez-vous dans le dossier du front :

``` bash
cd .\SohiaFront```

3. Lancez le serveur de développement Angular :

```bash
ng serve
```

L'application est désormais accessible via votre navigateur (par défaut
: http://localhost:4200).

------------------------------------------------------------------------

## Démonstration

Deux scénarios Cucumber sont fournis pour démontrer le bon
fonctionnement du backend. Ils se trouvent dans le fichier suivant :

Chemin du fichier : `src/test/resources/features/demo.feature`

Ces scénarios sont automatiquement exécutés lors d'un `mvn test` ou
`mvn package`.\
Vous pouvez également les exécuter manuellement avec :

``` bash
mvn test
```

Ou en les lançant directement depuis un IDE.

------------------------------------------------------------------------

## Structuration du projet

Ce projet suit l'architecture standard d'un projet Java géré par Maven.

### Organisation des dossiers

La structure des dossiers est la suivante :

-   `pom.xml` : Le fichier au cœur de notre projet Maven.\
-   `src/main/java` : Contient tout le code source de l'application
    (logique métier et serveurs API).\
-   `src/test/` : Contient tout ce qui est nécessaire pour les tests.
    -   `java` : Tests unitaires et définitions des steps Cucumber.\
    -   `resources` : Scénarios Cucumber au format `.feature`.\
-   `.github/` : Contient les configurations pour GitHub Actions
    (CI/CD).\
-   `doc/` : Contient les rendus.\
-   `SohiaFront/` : Contient le code source Angular du Frontend.

------------------------------------------------------------------------

## Kanban

Vous pouvez suivre l'avancement du projet sur notre tableau Kanban en
cliquant sur le lien ci-dessous :

[Accéder au Kanban du projet](https://github.com/orgs/PNS-Conception/projects/83/views/6)
