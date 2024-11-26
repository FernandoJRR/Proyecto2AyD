import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Business } from '../../models/Business';

@Injectable({
  providedIn: 'root',
})
export class BusinessService {
  private apiUrl = 'http://localhost:8080/api/negocio';

  constructor(private http: HttpClient) {}

  // Obtener todos los negocios
  getAllBusinesses(): Observable<Business[]> {
    console.log('Enviando request para obtener todos los negocios...');
    return this.http.get<{ data: Business[] }>(`${this.apiUrl}/public/getNegocios`).pipe(
      map((response) => {
        console.log('Respuesta obtenida de getAllBusinesses:', response); // Log de la respuesta completa
        return response.data;
      })
    );
  }

  // Obtener un negocio por ID
  getBusinessById(id: number): Observable<Business> {
    console.log(`Enviando request para obtener el recurso con ID: ${id}`);
    return this.http.get<{ data: Business }>(`${this.apiUrl}/public/${id}`).pipe(
      map(response => {
        console.log('Recurso obtenido por ID:', response.data);
        return response.data; // Devuelve solo el recurso en `data`
      })
    );
  }

  // Crear un nuevo negocio
  createBusiness(business: Business): Observable<Business> {
    console.log('Enviando request para crear negocio:', business); // Log para confirmar el objeto enviado
    return this.http.post<Business>(`${this.apiUrl}/private/crearNegocio`, business);
  }

  //SIN IMPLEMENTAR EN EL BACKEND
  // Actualizar un negocio existente
  updateBusiness(business: Business): Observable<void> {
    console.log('Enviando request para actualizar negocio:', business); // Log para confirmar el objeto enviado
    return this.http.patch<void>(`${this.apiUrl}/private/actualizarNegocio`, business);
  }

  // Eliminar un negocio por ID (Endpoint pendiente, pero dejamos el método preparado)
  deleteBusiness(id: number): Observable<void> {
    console.log(`Enviando request para eliminar el negocio con ID: ${id}`);
    return this.http.delete<void>(`${this.apiUrl}/private/${id}`);
  }
}
