import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Resource } from '../../models/Resource';
import { ResourceUnit } from '../../models/ResourceUnit';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  //private apiUrl = 'http://localhost:8080/api/recurso'; // URL base de la API (reemplázala con tu URL real)
  private apiUrl = environment.API_BASE_URL

  constructor(private http: HttpClient) {}

  // Obtener lista de recursos
  getAllResources(): Observable<Resource[]> {
    console.log('Enviando request para obtener todos los recursos...');
    return this.http.get<{ data: Resource[] }>(`${this.apiUrl}/recurso/public/getRecursos`).pipe(
      map(response => {
        console.log('Respuesta obtenida de getAllResources:', response); // Log de la respuesta completa
        return response.data;
      })
    );
  }

  // Obtener un recurso por ID
  getResourceById(id: number): Observable<Resource> {
    console.log(`Enviando request para obtener el recurso con ID: ${id}`);
    return this.http.get<{ data: Resource }>(`${this.apiUrl}/recurso/private/${id}`).pipe(
      map(response => {
        console.log('Recurso obtenido por ID:', response.data);
        return response.data; // Devuelve solo el recurso en `data`
      })
    );
  }

  getResourceUnitByResource(idResource: number): Observable<Array<ResourceUnit>> {
    return this.http.get<{ data: Array<ResourceUnit> }>(`${this.apiUrl}/unidad-recurso/private/recurso/${idResource}`).pipe(
      map(response => {
        console.log('Unidades Recurso obtenido por ID:', response.data);
        return response.data; // Devuelve solo el recurso en `data`
      })
    );
  }

  // Crear un nuevo recurso
  createResource(resource: Resource): Observable<Resource> {
    console.log('Enviando request para crear recurso:', resource); // Log para confirmar el objeto enviado
    return this.http.post<Resource>(`${this.apiUrl}/recurso/private/crearRecurso`, resource);
  }

  createResourceUnit(payload: any): Observable<ResourceUnit> {
    console.log('Enviando request para crear unidad recurso:', payload); // Log para confirmar el objeto enviado
    return this.http.post<ResourceUnit>(`${this.apiUrl}/unidad-recurso/private/crearUnidadRecurso`, payload);
  }

  //SIN IMPLEMENTAR EN EL BACKEND
  // Actualizar un recurso existente
  updateResource(resource: Resource): Observable<Resource> {
    console.log('Enviando request para actualizar recurso:', resource); // Log para confirmar el objeto enviado
    return this.http.put<Resource>(`${this.apiUrl}/recurso/${resource.id}`, resource);
  }

  // Eliminar un recurso por ID
  deleteResource(id: number): Observable<void> {
    console.log(`Enviando request para eliminar el recurso con ID: ${id}`);
    return this.http.delete<void>(`${this.apiUrl}/recurso/${id}`);
  }
}
