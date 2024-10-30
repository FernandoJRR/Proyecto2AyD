import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Resource } from '../../models/Resource';

@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  private apiUrl = 'http://localhost:8080/api/recurso'; // URL base de la API (reemplázala con tu URL real)

  constructor(private http: HttpClient) {}

  // Obtener lista de recursos
  getAllResources(): Observable<Resource[]> {
    console.log('Enviando request para obtener todos los recursos...');
    return this.http.get<{ data: Resource[] }>(`${this.apiUrl}/public/getRecursos`).pipe(
      map(response => {
        console.log('Respuesta obtenida de getAllResources:', response); // Log de la respuesta completa
        return response.data;
      })
    );
  }

  // Obtener un recurso por ID
  getResourceById(id: number): Observable<Resource> {
    console.log(`Enviando request para obtener el recurso con ID: ${id}`);
    return this.http.get<{ data: Resource }>(`${this.apiUrl}/private/${id}`).pipe(
      map(response => {
        console.log('Recurso obtenido por ID:', response.data);
        return response.data; // Devuelve solo el recurso en `data`
      })
    );
  }

  // Crear un nuevo recurso
  createResource(resource: Resource): Observable<Resource> {
    console.log('Enviando request para crear recurso:', resource); // Log para confirmar el objeto enviado
    return this.http.post<Resource>(`${this.apiUrl}/private/crearRecurso`, resource);
  }

  //SIN IMPLEMENTAR EN EL BACKEND
  // Actualizar un recurso existente
  updateResource(resource: Resource): Observable<Resource> {
    console.log('Enviando request para actualizar recurso:', resource); // Log para confirmar el objeto enviado
    return this.http.put<Resource>(`${this.apiUrl}/${resource.id}`, resource);
  }

  // Eliminar un recurso por ID
  deleteResource(id: number): Observable<void> {
    console.log(`Enviando request para eliminar el recurso con ID: ${id}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
