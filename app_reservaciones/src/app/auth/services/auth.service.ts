import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../utils/dialog/dialog.component';
import { UserStorageService } from '../../storages/user-storage.service';
import { RoleStorageService } from '../../storages/role-storage.service';
import { User } from '../../models/User';
import { Role } from '../../models/Role';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/usuario/public';

  constructor(
    private http: HttpClient,
    private userStorage: UserStorageService,
    private roleStorage: RoleStorageService,
    private dialog: MatDialog
  ) {}

  // Función para registrar un usuario
  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/crearUsuario`, data);
  }

  // Función para verificar un usuario con código de verificación
  verifyUser(code: any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/verificarUsuario`, { code });
  }

  // Función para iniciar sesión
  login(data: { email: string; password: string }): Observable<any> {
    console.log('Iniciando solicitud de login al backend');
    return this.http.post<any>(`${this.apiUrl}/login`, data).pipe(
      tap((response) => {
        if (response?.data?.usuario && response.data.jwt) {
          const usuario: User = response.data.usuario;
          usuario.verificado = response.data.validated;

          // Almacenando datos en los storages
          this.userStorage.setUser(usuario);
          // Mapear usuario.roles (UserRole[]) a un arreglo de Role[] antes de almacenarlo en el storage
          this.roleStorage.setRoles(usuario.roles.map(userRole => userRole.rol));
          //this.roleStorage.setRoles(usuario.roles);
          console.log('Usuario almacenado en el storage:', this.userStorage.getUser());
          console.log('Roles almacenados en el storage:', this.roleStorage.getRoles());

          // Guardando JWT en localStorage
          localStorage.setItem('jwt', response.data.jwt);
          console.log('JWT almacenado en localStorage:', localStorage.getItem('jwt'));
        }
      })
    );
  }

  // Función para recuperar contraseña
  resetPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/recuperarPasswordMail`, { email });
  }

  // Diálogo de confirmación
  openConfirmationDialog(
    title: string,
    description: string,
    backgroundColor: 'gray' | 'red' | 'green'
  ): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }

  // Almacenamiento temporal para autenticación de dos factores
  setTempUserData(userData: any, jwt: string): void {
    sessionStorage.setItem('tempUserData', JSON.stringify({ userData, jwt }));
    console.log('Datos temporales de 2FA almacenados en sessionStorage:', sessionStorage.getItem('tempUserData'));
  }

  // Función para obtener el JWT almacenado
  getJwt(): string | null {
    return localStorage.getItem('jwt');
  }

  // Cerrar sesión
  logout(): void {
    this.userStorage.clearUser();
    this.roleStorage.clearRoles();
    localStorage.removeItem('jwt');
    console.log('Sesión cerrada y datos eliminados de los storages');
  }
}
