# SopiaTech Eats-Team-25-26 

## TEAM O

PO : BOULET Olivier  (FISA)
SA : GUERRAOUI Ismail  (FISE)
QA : MENOU Clement  (FISE)
OPS : KARBAL Adam  (FISE)
QA & PO : MADANDAR ARANI Ali  (FISE)

## doc
- [Rendue TD1](doc/RenduTD1.pdf)
- [Rendue D0](doc/D0.pdf)
- [Rendue D1](doc/TeamO-renduD1.pdf)



## Lancer le projet et l’installer

### Prérequis

Avant de commencer, assurez-vous d’avoir installé :
- **Java 21** (ou version compatible)
- **Maven 3.9+**
- Un environnement de développement compatible (IntelliJ IDEA, VS Code, Eclipse, etc.)

---

### Compilation et exécution des tests

Le projet utilise **Maven** pour compiler le code, gérer les dépendances et exécuter automatiquement les tests unitaires et fonctionnels (**JUnit5 + Cucumber**).

Pour lancer une compilation complète et vérifier que tout fonctionne :
```bash
mvn clean package
```
### Démonstration

Deux scénarios Cucumber sont fournis pour démontrer le bon fonctionnement du backend.
Ils se trouvent dans le fichier suivant :

📄 **Chemin du fichier :** `src/test/resources/features/demo.feature`

Ces scénarios sont automatiquement exécutés lors d’un `mvn test` ou `mvn package`.
Vous pouvez également les exécuter manuellement avec :

```bash
mvn test
```
Ou juste en les lancant depuis un IDE.

##  Structuration du projet

Ce projet suit l'architecture standard d'un projet **Java** géré par **Maven**.

###  Organisation des dossiers

La structure des dossiers est la suivante :

* `pom.xml` : Le fichier au cœur de notre projet Maven. 

* `src/main/java` : Ce dossier contient **tout le code source de l'application**. C'est ici que se trouve la logique métier.

* `src/test/` : Ce répertoire regroupe tout ce qui est nécessaire pour tester notre application.
    * `java` : Contient le code des tests. Cela inclut nos tests unitaires avec **JUnit 5** et les définitions des étapes pour nos scénarios **Cucumber**.
    * `resources` : Contient les fichiers non-Java nécessaires aux tests, et plus particulièrement nos fichiers de scénarios (`.feature`) écrits en langage Gherkin pour Cucumber.

* `.github/` : Ce dossier contient les configurations spécifiques à GitHub, notamment les workflows d'intégration continue (CI/CD) avec GitHub Actions.

* `doc/` : Ce dossier est destiné à contenir tous les rendues.

## Kanban

Vous pouvez suivre l'avancement du projet sur notre tableau Kanban en cliquant sur le lien ci-dessous :

[Accéder au Kanban du projet](https://github.com/orgs/PNS-Conception/projects/83/views/6)
   

   
