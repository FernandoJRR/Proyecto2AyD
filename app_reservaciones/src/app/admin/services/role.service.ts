import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Role } from '../../models/Role';
import { RoleCreationRequest } from '../../models/RoleCreationRequest';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Obtener lista de roles
  getAllRoles(): Observable<Role[]> {
    return this.http.get<{ code: number; message: string; data: Role[] }>(`${this.apiUrl}/rol/public/getRoles`).pipe(
      map(response => response.data)
    );
  }

  // Crear un nuevo rol
  createRole(roleData: RoleCreationRequest): Observable<Role> {
    return this.http.post<Role>(`${this.apiUrl}/rol/private/crearRol`, roleData);
  }

  // Eliminar un rol por ID
  deleteRole(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/rol/private/${id}`);
  }
}
