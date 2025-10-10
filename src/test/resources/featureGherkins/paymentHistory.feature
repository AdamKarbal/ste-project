# language: fr
Fonctionnalité: Consultation de l'historique des commandes
  Afin de suivre mes achats passés
  En tant qu'utilisateur connecté
  Je veux pouvoir consulter la liste de mes commandes précédentes

  Scénario: Consultation réussie de l'historique des commandes
    Étant donné que l'utilisateur a passé plusieurs commandes
    Quand il veut accéder à son historique de commandes
    Alors toutes les commandes qu'il a réalisé et qui ont été payées s'affichent
    Et leur moyen de paiement s'affiche
