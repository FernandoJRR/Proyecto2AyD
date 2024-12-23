import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environment/environment";

@Injectable({
    providedIn: 'root'
})

export class GlobalService {
    apiUrl = environment.API_BASE_URL
    constructor(
        private http: HttpClient,
    ) {}

  getConfig(): Observable<any> {
    return this.http.get(`${this.apiUrl}/global_config/public/getConfig`);
  }

  updateConfig(payload: any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/global_config/private/actualizarConfiguracion`, payload);
  }

  updateImage(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/global_config/private/actualizarImagen`, payload);
  }
}