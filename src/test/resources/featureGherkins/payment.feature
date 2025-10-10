# language: fr
Fonctionnalité: Paiement d'une commande
  Afin de finaliser une commande
  En tant qu'utilisateur disposant d'un crédit
  Je veux que le montant de la commande soit déduit de mon crédit si j'ai assez d'argent

  Scénario: Paiement réussi d'une commande
    Quand l'utilisateur finalise sa commande et qu'il a assez d'argent pour la payer
    Et que l'état de la commande est PAID
    Alors le prix de la commande est retiré de son crédit



