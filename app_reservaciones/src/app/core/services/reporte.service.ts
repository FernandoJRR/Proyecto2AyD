import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environment/environment";

@Injectable({
    providedIn: 'root'
})

export class ReporteService {
    apiUrl = environment.API_BASE_URL
    constructor(
        private http: HttpClient,
    ) {}

  getConteoReport(tipoTiempo: string, cantidad: number, estado: string | null): Observable<any> {
      if (estado) {
        return this.http.get(`${this.apiUrl}/reporte/public/contarReservaciones?tipoTiempo=${tipoTiempo}&cantidad=${cantidad}&estado=${estado}`);
      }

        return this.http.get(`${this.apiUrl}/reporte/public/contarReservaciones?tipoTiempo=${tipoTiempo}&cantidad=${cantidad}`);
  }

  updateConfig(payload: any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/global_config/private/actualizarConfiguracion`, payload);
  }

  updateImage(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/global_config/private/actualizarImagen`, payload);
  }
}