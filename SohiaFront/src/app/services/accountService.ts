import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import {AccountUser, AccountUserCreated} from '../models/accountUser';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private accountUrl = 'http://localhost:8005/api/accountRepo';
  constructor(private httpClient: HttpClient) {}

  createUser(nom: string): Observable<AccountUserCreated> {
    const body = {
      name: nom,
      credit: 30.0
    };

    return this.httpClient.post<AccountUserCreated>(`${this.accountUrl}/user/`, body);
  }

  getUser(id:number):Observable<AccountUser>{
    return this.httpClient.get<AccountUser>(`${this.accountUrl}/user/{id}/`)
  }
}
