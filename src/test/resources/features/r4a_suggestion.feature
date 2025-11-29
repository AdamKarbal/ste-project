Feature: R4a - Assistance intelligente (suggestions & création de plat)

  Background:
    Given un RestaurantManager initialisé pour "Chez Test"
    And un référentiel de suggestions
      | keyword              | category     | type   | suggestedPrice | tags        | toppings                                  |
      | Pizza Margherita     | MAIN_COURSE  | PIZZA  | 10.0           | VEGETARIAN  | Mozzarella:1.2; Basilic:0.5               |
      | Couscous royal       | MAIN_COURSE  | MEAT   | 12.5           | HALAL       | Pois chiches:0.8; Légumes:1.0             |
      | Crème brûlée         | DESSERT      | OTHER  | 5.0            |             |                                            |
      | Spaghetti bolognaise | MAIN_COURSE  | PASTA  | 11.0           |             | Parmesan:1.0                              |
      | Tacos poulet         | MAIN_COURSE  | OTHER  | 9.0            |             | Guacamole:1.2; Fromage:1.0                |

  Scenario: Prévisualiser une suggestion (Pizza Margherita)
    When je prévisualise la suggestion pour "Pizza Margherita"
    Then la catégorie suggérée est "MAIN_COURSE"
    And le type suggéré est "PIZZA"
    And un prix moyen est proposé
    And les toppings suggérés contiennent "Mozzarella"
    And les tags suggérés contiennent "VEGETARIAN"

  Scenario: Création avec saisie qui écrase le prix et merge les toppings
    When je saisis un plat avec
      | name   | Pizza Margherita |
      | price  | 6.0             |
      | toppings | Olives:0.8     |
    And je crée le plat via le RestaurantManager
    Then le plat créé a pour type "PIZZA"
    And le prix final est "6.0"
    And les toppings finaux contiennent "Mozzarella"
    And les toppings finaux contiennent "Basilic"
    And les toppings finaux contiennent "Olives"
    And le restaurant contient exactement 1 plat

  Scenario: Pas de doublon si l’utilisateur re-saisit un topping déjà suggéré
    When je saisis un plat avec
      | name     | Pizza Mozzarella |
      | toppings | Mozzarella:1.2   |
    And je crée le plat via le RestaurantManager
    Then la liste des toppings finaux contient une seule fois "Mozzarella"

  Scenario: Mot-clé inconnu -> on s’appuie sur la saisie utilisateur
    When je saisis un plat avec
      | name     | Plat mystère |
      | category | MAIN_COURSE  |
      | type     | OTHER        |
      | price    | 9.9          |
    And je crée le plat via le RestaurantManager
    Then le plat créé a pour nom "Plat mystère"
    And le type final est "OTHER"
    And la catégorie finale est "MAIN_COURSE"
    And le prix final est "9.9"

  Scenario: Prix négatif refusé
    When je saisis un plat avec
      | name  | Burger |
      | price | -1.0   |
    And je tente de créer le plat via le RestaurantManager
    Then une erreur "Price must be >= 0" est levée

  Scenario: Doublon de plat (même nom) interdit
    When je saisis un plat avec
      | name | Pizza Royale |
    And je crée le plat via le RestaurantManager
    And je tente de créer le plat via le RestaurantManager
    Then une erreur de type "DishInDishes" est levée
