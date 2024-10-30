import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';
import { UserStorageService } from '../../../storages/user-storage.service';
import { RoleStorageService } from '../../../storages/role-storage.service';
import { PermissionStorageService } from '../../../storages/permission-storage.service';

@Component({
  selector: 'app-admin-view',
  templateUrl: './admin-view.component.html',
  styleUrls: ['./admin-view.component.css'],
})
export class AdminViewComponent {
  opened = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private userStorage: UserStorageService,
    private roleStorage: RoleStorageService,
    private permissionsStorage: PermissionStorageService //PEND: Cuando se restringa el accceso a modulos del SIDE-BAR segun los permisos

  ) {}

  toggleSidebar() {
    this.opened = !this.opened;
  }

  logout() {
    console.log('Ejecutando cierre de sesión...');
    this.authService.logout(); // Borra el JWT
    this.userStorage.clearUser(); // Borra la información del usuario
    this.roleStorage.clearRoles(); // Borra la información de los roles
    this.permissionsStorage.clearPermissions(); // PENDIENTE 
    this.router.navigate(['/auth/login']); // Redirige al login
  }
}
