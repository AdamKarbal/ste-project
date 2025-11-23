import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private router: Router) {}

  /** Connexion instantanée */
  login(): void {
    this.isLoggedInSubject.next(true);
    alert('👋 Team O — Vous êtes maintenant connecté !');
  }

  /** Déconnexion avec redirection */
  logout(): void {
    this.isLoggedInSubject.next(false);
    alert('👋 Vous êtes maintenant déconnecté !');
    this.router.navigate(['/home']);
  }
}
