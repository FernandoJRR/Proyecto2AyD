import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Resource } from '../../models/Resource';

@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  private apiUrl = 'https://localhost:8080/api/recurso/private'; // URL base de la API (reemplázala con tu URL real)

  constructor(private http: HttpClient) {}

  // Obtener lista de recursos
  getAllResources(): Observable<Resource[]> {
    return this.http.get<Resource[]>(`${this.apiUrl}/public/getRecursos`);
  }

  // Obtener un recurso por ID
  getResourceById(id: number): Observable<Resource> {
    return this.http.get<Resource>(`${this.apiUrl}/${id}`);
  }

  // Crear un nuevo recurso
  createResource(resource: Resource): Observable<Resource> {
    return this.http.post<Resource>(`${this.apiUrl}`, resource);
  }

  // Actualizar un recurso existente
  updateResource(resource: Resource): Observable<Resource> {
    return this.http.put<Resource>(`${this.apiUrl}/${resource.id}`, resource);
  }

  // Eliminar un recurso por ID
  deleteResource(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
