import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Service } from '../../models/Service';

@Injectable({
  providedIn: 'root',
})
export class ServiceService {
  private apiUrl = 'http://localhost:8080/api/servicio';

  constructor(private http: HttpClient) {}

  // Obtener lista de servicios
  getAllServices(): Observable<Service[]> {
    return this.http.get<{ data: Service[] }>(`${this.apiUrl}/public/getServicios`).pipe(
      map(response => response.data)
    );
  }

  // Eliminar servicio por ID
  deleteService(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/private/deleteServicio/${id}`);
  }
}
