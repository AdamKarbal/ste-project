
Feature: Démonstration de notre système de commande de repas

  Background:
    Given un catalogue de restos
      | name        | cuisineType  | establishmentType | dish1     | price1 | dish2    | price2 | start1 | end1  | capacity1 | start2 | end2  | capacity2 | tags1                           | tags2                      |
      | Pasta Mia   | ITALIENNE    | RESTAURANT        | Spaghetti | 12.5   | Lasagne  | 14.0   | 11:30  | 14:30 | 10        | 20:00  | 22:00 | 120       | VEGETARIAN, LACTOSE_FREE        |                            |
      | SushiZen    | JAPONAISE    | RESTAURANT        | Maki      | 9.0    | Sashimi  | 15.0   | 12:00  | 15:00 | 80        | 18:30  | 21:30 | 90        | SPICY                           | GLUTEN_FREE                |
      | MammaRita   | ITALIENNE    | RESTAURANT        | Pizza     | 11.0   | Gnocchi  | 13.0   | 22:00  | 02:00 | 110       | 11:00  | 14:00 | 100       | Vegan; Organic                  | Vegetarian                 |
      | KebabKorner | VIETNAMIENNE | FAST_FOOD         | Pizza     | 11.0   | Gnocchi  | 13.0   | 22:00  | 02:00 | 110       | 11:00  | 14:00 | 100       |                                 |                            |

  Scenario: Un client passe une commande et la paye avec son crédit
    Given que le restaurant "Pasta Mia" propose le plat "Spaghetti" à 12.5€
    And que "Pasta Mia" a un créneau de 20:00 à 22:00 avec une capacité de 120 repas
    And que le client "Younes" est connecté à son compte
    And son crédit est de 10000.0€
    When "Younes" recherche les restaurants de type "ITALIENNE"
    And il sélectionne le restaurant "Pasta Mia"
    And il ajoute 1 "Spaghetti" à son panier
    And il procède au paiement avec son crédit
    Then le paiement doit être accepté et une commande payeé par cash doit être créée pour "Younes"
    And l'historique de commandes de "Younes" doit contenir 1 commande

  Scenario: Un restaurateur crée et configure son restaurant
    Given le restaurateur "Nico" à été enregistré
    When "Nico" crée son restaurant "Les delices de new delices"
    When Il le configure : cuisineType "INDIENNE"
    When Il le configure : establishmentType "RESTAURANT"
    When Il ajoute le plat "Riz coco curry" à 12.5€
    When Il ajoute un creneau de 11:30 à 12:00 avec 5 capacités
    Then Le restaurant doit être disponible sous le nom de "Les delices de new delices"
    Then "Younes" peut passer trouver le restaurant "Les delices de new delices"
    Then "Younes" peut trouver le plat "Riz coco curry"
