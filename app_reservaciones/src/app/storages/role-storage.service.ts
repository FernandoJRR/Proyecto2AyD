import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Role } from '../models/Role';

@Injectable({
  providedIn: 'root'
})
export class RoleStorageService {
  private rolesSubject = new BehaviorSubject<Role[]>([]);
  roles$ = this.rolesSubject.asObservable();

  setRoles(roles: Role[]): void {
    this.rolesSubject.next(roles);
    localStorage.setItem('roles', JSON.stringify(roles));
  }

  getRoles(): Role[] {
    const roles = localStorage.getItem('roles');
    return roles ? JSON.parse(roles) : this.rolesSubject.getValue();
  }

  clearRoles(): void {
    this.rolesSubject.next([]);
    localStorage.removeItem('roles');
  }
}
