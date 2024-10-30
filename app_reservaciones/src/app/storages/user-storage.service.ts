import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User } from '../models/User';

@Injectable({
  providedIn: 'root'
})
export class UserStorageService {
  private userSubject = new BehaviorSubject<User | null>(null);
  user$ = this.userSubject.asObservable();

  setUser(user: User): void {
    this.userSubject.next(user);
    localStorage.setItem('user', JSON.stringify(user)); // Opcional: almacenamiento persistente
  }

  getUser(): User | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : this.userSubject.getValue();
  }

  clearUser(): void {
    this.userSubject.next(null);
    localStorage.removeItem('user');
  }
}
