Feature: Filtrer les restaurants par type de cuisine

  Scenario: Filtre par cuisine italienne
    Given un catalogue de restaurants
      | name      | cuisineType | establishmentType | dish1     | price1 | dish2    | price2 | start1 | end1  | capacity1 | start2 | end2  | capacity2 |
      | Pasta Mia | ITALIENNE   | RESTAURANT        | Spaghetti | 12.5   | Lasagne  | 14.0   | 11:30  | 14:30 | 100       | 19:00  | 22:00 | 120       |
      | SushiZen  | JAPONAISE   | RESTAURANT        | Maki      | 9.0    | Sashimi  | 15.0   | 12:00  | 15:00 | 80        | 18:30  | 21:30 | 90        |
      | MammaRita | ITALIENNE   | RESTAURANT        | Pizza     | 11.0   | Gnocchi  | 13.0   | 22:00  | 02:00 | 110       | 11:00  | 14:00 | 100       |

    When je filtre les restaurants par cuisine "italienne"
    Then je vois exactement 2 restaurants
    And la liste contient "Pasta Mia"
    And la liste contient "MammaRita"
