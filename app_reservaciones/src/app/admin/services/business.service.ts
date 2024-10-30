import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Business } from '../../models/Business';

@Injectable({
  providedIn: 'root'
})
export class BusinessService {
  private apiUrl = 'http://localhost:8080/api/negocio';

  constructor(private http: HttpClient) {}

  // Obtener todos los negocios
  getAllBusinesses(): Observable<Business[]> {
    return this.http.get<{ data: Business[] }>(`${this.apiUrl}/public/getNegocios`).pipe(
      map((response) => response.data)
    );
  }

  // Obtener un negocio por ID
  getBusinessById(id: number): Observable<Business> {
    return this.http.get<Business>(`${this.apiUrl}/public/${id}`);
  }

  // Crear un nuevo negocio
  createBusiness(business: Business): Observable<Business> {
    return this.http.post<Business>(`${this.apiUrl}/private/crearNegocio`, business);
  }

  // Actualizar un negocio existente
  updateBusiness(business: Business): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/private/${business.id}`, business);
  }

  // Eliminar un negocio por ID (Endpoint pendiente, pero dejamos el método preparado)
  deleteBusiness(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/private/${id}`);
  }
}
