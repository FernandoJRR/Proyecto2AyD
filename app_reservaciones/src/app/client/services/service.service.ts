import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Service } from '../../models/Service';
import { Resource } from '../../models/Resource';
import { Day } from '../../models/Day';
import { ServiceCreationRequest } from '../../models/ServiceCreationRequest';
import { environment } from '../../../environment/environment';
import { User } from '../../models/User';

@Injectable({
  providedIn: 'root',
})
export class ServiceService {
  private apiUrl = environment.API_BASE_URL

  constructor(private http: HttpClient) {}

  // Obtener lista de servicios
  getServicesByBusinessId(id: Number): Observable<Service[]> {
    return this.http.get<{ data: Service[] }>(`${this.apiUrl}/servicio/public/negocio/${id}`).pipe(
      map(response => response.data)
    );
  }

  // Obtener lista de encargados de un servicio
  getEncargadosByService(id: Number): Observable<User[]> {
    return this.http.get<{ data: User[] }>(`${this.apiUrl}/servicio/public/encargados/${id}`).pipe(
      map(response => response.data)
    );
  }

  // Obtener lista de encargados de un servicio
  getUnidadesRecursoByService(id: Number): Observable<User[]> {
    return this.http.get<{ data: User[] }>(`${this.apiUrl}/servicio/public/unidadesRecurso/${id}`).pipe(
      map(response => response.data)
    );
  }
}
