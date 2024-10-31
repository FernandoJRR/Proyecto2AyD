import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Service } from '../../models/Service';
import { Resource } from '../../models/Resource';
import { Day } from '../../models/Day';
import { ServiceCreationRequest } from '../../models/ServiceCreationRequest';

@Injectable({
  providedIn: 'root',
})
export class ServiceService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Obtener lista de servicios
  getAllServices(): Observable<Service[]> {
    return this.http.get<{ data: Service[] }>(`${this.apiUrl}/servicio/public/getServicios`).pipe(
      map(response => response.data)
    );
  }

  createService(serviceData: ServiceCreationRequest): Observable<any> {
    console.log('Enviando datos de servicio para creación:', serviceData);
    return this.http.post(`${this.apiUrl}/servicio/private/crearServicio`, serviceData);
  }

  // Eliminar servicio por ID
  deleteService(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/private/deleteServicio/${id}`);
  }

  getServiceTypes(): Observable<any[]> {
    console.log('Solicitando tipos de servicio...');
    return this.http.get<{ data: any[] }>(`${this.apiUrl}/tipo-servicio/public/getTiposServicio`).pipe(
      map(response => response.data)
    );
  }

  getResources(): Observable<Resource[]> {
    console.log('Solicitando lista de recursos...');
    return this.http.get<{ data: Resource[] }>(`${this.apiUrl}/recurso/public/getRecursos`).pipe(
      map(response => response.data)
    );
  }

  getDays(): Observable<Day[]> {
    console.log('Solicitando días de atención...');
    return this.http.get<{ data: Day[] }>(`${this.apiUrl}/dia/public/getDias`).pipe(
      map(response => response.data)
    );
  }

}
