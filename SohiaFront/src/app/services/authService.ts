import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import {AccountService} from './accountService';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();
  private lastUserIdSubject = new BehaviorSubject<number | null>(null);
  lastUserId$ = this.lastUserIdSubject.asObservable();
  constructor(private router: Router,private accountService : AccountService) {}

  login(): void {
    const clientName = 'client'+(this.lastUserIdSubject.value ?? 0);
    this.accountService.createUser(clientName).subscribe({
      next: (user) => {
        this.lastUserIdSubject.next(user.id);
        console.log(user.id);
        this.isLoggedInSubject.next(true);
      }
    });
    alert('👋 Team O — Vous êtes maintenant connecté !');
  }
  logout(): void {
    this.isLoggedInSubject.next(false);
    this.router.navigate(['/home']);
  }
}
