import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Business } from '../../models/Business';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root',
})
export class BusinessService {
  private apiUrl = environment.API_BASE_URL

  constructor(private http: HttpClient) {}

  // Obtener todos los negocios
  getAllBusinesses(): Observable<Business[]> {
    console.log('Enviando request para obtener todos los negocios...');
    return this.http.get<{ data: Business[] }>(`${this.apiUrl}/negocio/public/getNegocios`).pipe(
      map((response) => {
        console.log('Respuesta obtenida de getAllBusinesses:', response); // Log de la respuesta completa
        return response.data;
      })
    );
  }
}
