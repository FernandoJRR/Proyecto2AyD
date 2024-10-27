import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../utils/dialog/dialog.component';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/usuario/public'; // Cambiar por la URL real de la API

  constructor(private http: HttpClient, private dialog: MatDialog) {}

  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/crearUsuario`, data);
  }

  //validateAccount(){}

  login(data: { email: string; password: string }): Observable<any> { // Endpoint para iniciar sesion
    return this.http.post(`${this.apiUrl}/login`, data);
  }

  resetPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/recuperarPasswordMail`, { email });  // Endpoint correcto para recuperar contraseña
  }

  openConfirmationDialog(title: string, description: string, backgroundColor: 'gray' | 'light-red' | 'light-green'): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor }
    });
  }
}
