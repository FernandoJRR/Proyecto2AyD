import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { map, tap } from 'rxjs/operators';
import { DialogComponent } from '../../utils/dialog/dialog.component';
import { UserStorageService } from '../../storages/user-storage.service';
import { RoleStorageService } from '../../storages/role-storage.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/usuario/public'; // Cambiar por la URL real de la API

  constructor(
    private http: HttpClient,
    private userStorage: UserStorageService,
    private roleStorage: RoleStorageService,
    private dialog: MatDialog
  ) {}

  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/crearUsuario`, data);
  }

  verifyUser(code: any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/verificarUsuario`, { code });
  }

  login(data: { email: string; password: string }): Observable<any> {
    console.log('Iniciando solicitud de login al backend'); // Log inicial en el servicio de login
    return this.http.post<any>(`${this.apiUrl}/login`, data).pipe(
      tap((response) => {
        console.log('Respuesta recibida del backend:', response); // Verificar la respuesta del backend
        if (response?.usuario && response.jwt) {
          this.userStorage.setUser(response.usuario);
          this.roleStorage.setRoles(response.usuario.roles);
          localStorage.setItem('jwt', response.jwt); // JWT almacenado en localStorage
        }
      })
    );
  }

  resetPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/recuperarPasswordMail`, { email }); // Endpoint correcto para recuperar contraseña
  }

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
  }

  // Creación de sesión con JWT y usuario
  createSession(userData: any, jwt: string): void {
    sessionStorage.setItem('userData', JSON.stringify(userData));
    sessionStorage.setItem('jwt', jwt);
  }

  // Función para obtener el JWT almacenado
  getJwt(): string | null {
    return sessionStorage.getItem('jwt');
  }

  // Cerrar sesión
  logout(): void {
    sessionStorage.removeItem('userData');
    sessionStorage.removeItem('jwt');
  }
}
