import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'https://tu-api-url.com/api/usuario/public'; // Cambiar por la URL real de la API

  constructor(private http: HttpClient) {}

  login(data: { email: string; password: string }): Observable<any> { // Endpoint para iniciar sesion
    return this.http.post(`${this.apiUrl}/login`, data);
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/crearUsuario`, data);  // Endpoint para registrar un usuario
  }

  resetPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/recuperarPasswordMail`, { email });  // Endpoint correcto para recuperar contraseña
  }
}
