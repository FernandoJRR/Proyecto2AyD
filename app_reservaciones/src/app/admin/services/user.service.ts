import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
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

  getAdminUsers(): Observable<Array<User>> {
    return this.http.get<{data: Array<User>}>(`${this.apiUrl}/private/getUsuarios`).pipe(
        map(response => response.data)
    );
  }

  getUser(userId: number): Observable<User> {
    return this.http.get<{data: User}>(`${this.apiUrl}/public/${userId}`).pipe(
      map(response => response.data)
    );
  }

  createHelper(helperData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/private/crearAyudante`, helperData);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/private/eliminarUsuario/${id}`);
  }

  updateUser(userData: any): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/private/all/updateUsuario`, userData);
  }

  updateUserFull(userData: any): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/private/updateUsuario`, userData);
  }
}
