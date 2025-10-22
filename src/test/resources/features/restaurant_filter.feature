Feature: Filtrer les restaurants

  Background:
    Given un catalogue de restaurants
      | name        | cuisineType | establishmentType | dish1     | price1 | dish2    | price2 | start1 | end1  | capacity1 | start2 | end2  | capacity2 | tags1                           | tags2                      |
      | Pasta Mia   | ITALIENNE   | RESTAURANT        | Spaghetti | 12.5   | Lasagne  | 14.0   | 11:30  | 14:30 | 100       | 19:00  | 22:00 | 120       | VEGETARIAN, LACTOSE_FREE        |                            |
      | SushiZen    | JAPONAISE   | RESTAURANT        | Maki      | 9.0    | Sashimi  | 15.0   | 12:00  | 15:00 | 80        | 18:30  | 21:30 | 90        | SPICY                           | GLUTEN_FREE                |
      | MammaRita   | ITALIENNE   | RESTAURANT        | Pizza     | 11.0   | Gnocchi  | 13.0   | 22:00  | 02:00 | 110       | 11:00  | 14:00 | 100       | Vegan; Organic                  | Vegetarian                 |
      | KebabKorner | VIETNAMIENNE| FAST_FOOD         | Pizza     | 11.0   | Gnocchi  | 13.0   | 22:00  | 02:00 | 110       | 11:00  | 14:00 | 100       |                                 |                            |

  Scenario: Filtre par cuisine italienne
    When je filtre les restaurants par cuisine "italienne"
    Then je vois exactement 2 restaurants
    And la liste contient "Pasta Mia"
    And la liste contient "MammaRita"

  Scenario: Filtre par restaurants ouverts
    When je filtre les restaurants par horaire "18:30"
    Then je vois exactement 1 restaurants
    And la liste contient "SushiZen"

    Scenario: Filtre par type de restaurants
      When je filtre les restaurants par etablissement "fast_food"
      Then je vois exactement 1 restaurants
      And la liste contient "KebabKorner"

      Scenario: Filtre par prix max
        When je filtre les restaurants avec un prix max de 10 euros
        Then je vois exactement 1 restaurants
        And la liste contient "SushiZen"

      Scenario: Filtre par gamme de prix
        When je filtre les restaurants avec un prix entre 8 et 11 euros
        Then je vois exactement 3 restaurants
        And la liste contient "SushiZen"
        And la liste contient "KebabKorner"
        And la liste contient "MammaRita"

      Scenario: Filtre par prix min
        When je filtre les restaurants avec un prix min de 14.5 euros
        Then je vois exactement 1 restaurants
        And la liste contient "SushiZen"

      Scenario: Filtre par nom
        When je filtre les restaurants par nom "Mia"
        Then je vois exactement 1 restaurants
        And la liste contient "Pasta Mia"

      Scenario: Filtre par tag (enum)
        When je filtre les restaurants par tag "VEGETARIAN"
        Then je vois exactement 2 restaurants
        And la liste contient "Pasta Mia"
        And la liste contient "MammaRita"

        Scenario: Filtre par tag (label)
          When je filtre les restaurants par tag "Gluten-free"
          Then je vois exactement 1 restaurants
          And la liste contient "SushiZen"

        Scenario: Filtre par tous les tags (enum)
          When je filtre les restaurants par tous les tags "VEGETARIAN, LACTOSE_FREE"
          Then je vois exactement 1 restaurants
          And la liste contient "Pasta Mia"

        Scenario: Filtre par tous les tags (labels)
          When je filtre les restaurants par tous les tags "Vegan; Organic"
          Then je vois exactement 1 restaurants
          And la liste contient "MammaRita"

        Scenario: Recherche multi-critères pas à pas
          When je commence une recherche
          And j'applique le filtre cuisine "ITALIENNE"
          And j'applique le filtre prix max 12.0
          And j'applique le filtre ouvert a "12:15"
          Then je vois exactement 1 restaurants
          And la liste contient "MammaRita"

        Scenario: Tag végétarien ET sans lactose à une heure donnée
          When je commence une recherche
          And j'applique le filtre tous les tags "VEGETARIAN, LACTOSE_FREE"
          And j'applique le filtre ouvert a "12:15"
          Then je vois exactement 1 restaurants
          And la liste contient "Pasta Mia"


        Scenario: Nom partiel + type
                When je commence une recherche
                And j'applique le filtre nom contenant "Mia"
                And j'applique le filtre etablissement "RESTAURANT"
                Then je vois exactement 1 restaurants
                And la liste contient "Pasta Mia"

