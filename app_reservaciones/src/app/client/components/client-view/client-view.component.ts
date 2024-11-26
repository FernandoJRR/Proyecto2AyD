import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';
import { UserStorageService } from '../../../storages/user-storage.service';
import { RoleStorageService } from '../../../storages/role-storage.service';
import { PermissionStorageService } from '../../../storages/permission-storage.service';
import { GlobalService } from '../../../core/services/global.service';
@Component({
  selector: 'app-client-view',
  templateUrl: './client-view.component.html',
  styleUrl: './client-view.component.css',
})
export class ClientViewComponent implements OnInit {
  opened = false;

  constructor(
    private globalService: GlobalService,
    private authService: AuthService,
    private router: Router,
    private userStorage: UserStorageService,
    private roleStorage: RoleStorageService,
    private permissionsStorage: PermissionStorageService //PEND: Cuando se restringa el accceso a modulos del SIDE-BAR segun los permisos
  ) {}

  configData = { siteName: "Booking App", logoUrl: ""};

  itemsMenu = [{label: 'Home', icon: 'pi pi-home', route: '/client'}, {label: 'Mis Reservaciones', icon: 'pi pi-calendar-clock', route: '/client/reservaciones'}]

  ngOnInit(): void {
      this.globalService.getConfig().subscribe((result) => {
        this.configData = { siteName: result.data.nombre, logoUrl: result.data.imagenString };
      })
  }

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
