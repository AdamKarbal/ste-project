Feature: Gestion des commandes clients

  Background:
    Given un restaurant nommé "AYO les Fratés"
    And que ce restaurant propose les plats suivants :
      | Nom du plat            | Type   | Catégorie      | Tag    | Prix  |
      | Burgouz                | MEAT   | MAIN_COURSE    | VEGAN  | 12.50 |
      | La Pizza de la Madre   | PIZZA  | MAIN_COURSE    | SPICY  | 14.00 |
    And qu’un client nommé "Adam" est enregistré sur la plateforme
    And que le restaurant "AYO les Fratés" contient bien ces plats dans sa carte

  # ===========================================================
  # 1️⃣ SCÉNARIO : Création et ajout de plats
  # ===========================================================

  Scenario: Un client crée une commande et ajoute deux plats du même restaurant
    Given qu’Adam démarre une nouvelle commande au restaurant "AYO les Fratés"
    When Adam ajoute le plat "Burgouz" en 1 exemplaire
    And Adam ajoute le plat "La Pizza de la Madre" en 1 exemplaire
    Then la commande doit contenir 2 plats au total
    And le prix total de la commande doit être de 26.50 euros
    And l’état de la commande doit passer à "IN_PREPARATION"

  # ===========================================================
  # 3️⃣ SCÉNARIO : Mise à jour et suppression de plats
  # ===========================================================

  Scenario: Un client modifie la quantité et retire un plat de sa commande
    Given qu’Adam a ajouté "Burgouz" et "La Pizza de la Madre" dans sa commande
    When Adam met à jour la quantité de "Burgouz" à 2
    Then le prix total doit être de 39.00 euros

    When Adam supprime "La Pizza de la Madre" de la commande
    Then la commande ne contient plus qu’un seul plat
    And le prix total doit être de 25.00 euros

  # ===========================================================
  # 4️⃣ SCÉNARIO : Confirmation de la commande
  # ===========================================================

  Scenario: Un client confirme sa commande avec un point de livraison et un créneau
    Given qu’Adam a une commande en cours avec les plats du restaurant "AYO les Fratés"
    And que le point de livraison "CAFETERIA" est défini
    And qu’un créneau de livraison valide est disponible entre 12h00 et 12h30
    When Adam confirme sa commande
    Then la commande doit être validée
    And l’état de la commande devient "PAID"
    And l’heure de livraison prévue doit être définie après le créneau choisi

  # ===========================================================
  # 5️⃣ SCÉNARIO : Échec de confirmation sans point de livraison
  # ===========================================================

  Scenario: Un client ne peut pas confirmer sa commande sans point de livraison
    Given qu’Adam a une commande en cours avec un créneau horaire choisi
    When Adam essaie de confirmer sa commande sans point de livraison
    Then la commande ne doit pas être validée
    And l’état reste inchangé
