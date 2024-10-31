import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../../models/User';

export interface UserResponse {
  code: number;
  message: string;
  data: User[];
  warning?: any;
  error?: any;
  errors?: any;
  warnings?: any;
}



@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/usuario';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.apiUrl}/private/getUsuarios`);
  }

  createHelper(helperData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/private/crearAyudante`, helperData);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/private/eliminarUsuario/${id}`);
  }
}
