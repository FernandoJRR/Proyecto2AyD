import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Permission } from '../models/Permission';

@Injectable({
  providedIn: 'root'
})
export class PermissionStorageService {
  private permissionsSubject = new BehaviorSubject<Permission[]>([]);
  permissions$ = this.permissionsSubject.asObservable();

  setPermissions(permissions: Permission[]): void {
    this.permissionsSubject.next(permissions);
    localStorage.setItem('permissions', JSON.stringify(permissions));
  }

  getPermissions(): Permission[] {
    const permissions = localStorage.getItem('permissions');
    return permissions ? JSON.parse(permissions) : this.permissionsSubject.getValue();
  }

  clearPermissions(): void {
    this.permissionsSubject.next([]);
    localStorage.removeItem('permissions');
  }
}
