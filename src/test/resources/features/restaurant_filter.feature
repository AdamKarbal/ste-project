Feature: Filtrer les restaurants par type de cuisine

  Scenario: Filtre par cuisine italienne
    Given un catalogue de restaurants
      | nom        | cuisines   | type        |
      | Pasta Mia  | ITALIENNE  | RESTAURANT  |
      | SushiZen   | JAPONAISE  | RESTAURANT  |
      | MammaRita  | ITALIENNE  | RESTAURANT  |
    When je filtre les restaurants par cuisine "italienne"
    Then je vois exactement 2 restaurants
    And la liste contient "Pasta Mia"
    And la liste contient "MammaRita"
