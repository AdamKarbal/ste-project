import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import {AccountUser} from '../models/accountUser';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private accountUrl = 'http://localhost:8005/api/accountRepo';

  constructor(private httpClient: HttpClient) {}

  createUser(nom: string): Observable<AccountUser> {
    const body = { name: nom };

    return this.httpClient.post<AccountUser>(`${this.accountUrl}/user/`, body);
  }
}
