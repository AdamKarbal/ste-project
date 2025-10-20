
Feature: Historique des commandes
  Afin de consulter mes commandes passées
  En tant qu'utilisateur
  Je veux accéder à l'historique de mes commandes

  Scenario: Affichage des commandes payées et des moyens de paiement
    Given l'utilisateur a passé plusieurs commandes
    When il veut accéder à son historique de commandes
    Then toutes les commandes qu'il a réalisé et qui ont été payées s'affichent
    And les bons moyens de paiement s'affichent
  Scenario: : Le client paie une commande avec son crédit
    Given un client dispose d'un crédit de 20 euros
    And il a passé une commande de 12 euros
    When il paie la commande avec son crédit
    Then son crédit doit être débité de 12 euros
