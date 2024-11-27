import { Injectable } from "@angular/core";
import { environment } from "../../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { Reservation } from "../../models/Reservation";
import { Cancelation } from "../../models/Cancelation";

@Injectable({
    providedIn: 'root'
})

export class ReservacionService {
    apiUrl = environment.API_BASE_URL
    constructor(
        private http: HttpClient,
    ) {}

  getMetodosPago(): Observable<Array<any>> {
    return this.http.get<{data: Array<any>}>(`${this.apiUrl}/reservacion/public/getMetodosPago`).pipe(
        map(response => response.data)
    );
  }

  getLinkComprobanteReservacion(idReservacion: Number): string {
      return `${this.apiUrl}/reservacion/public/comprobante/${idReservacion}`
  }

  getLinkFactura(idReservacion: Number): string {
      return `${this.apiUrl}/reservacion/public/factura/${idReservacion}`
  }

  getLinkFacturaCancelacion(idReservacion: Number): string {
      return `${this.apiUrl}/reservacion/public/cancelacion/${idReservacion}`
  }

  crearReservacion(payload: any): Observable<Reservation> {
    return this.http.post<{data: Reservation}>(`${this.apiUrl}/reservacion/public/crearReservacion`, payload).pipe(
        map(response => response.data)
    );
  }

  getReservacion(idReservacion: number): Observable<Reservation> {
    return this.http.get<{data: Reservation}>(`${this.apiUrl}/reservacion/public/${idReservacion}`).pipe(
        map(response => response.data)
    );
  }

  getAllReservacion(): Observable<Array<Reservation>> {
    return this.http.get<{data: Array<Reservation>}>(`${this.apiUrl}/reservacion/public/reservaciones`).pipe(
        map(response => response.data)
    );
  }

  getCancelacion(idReservacion: number): Observable<Cancelation> {
    return this.http.get<{data: Cancelation}>(`${this.apiUrl}/reservacion/public/getCancelacion/${idReservacion}`).pipe(
        map(response => response.data)
    );
  }

  getReservacionesByCliente(idCliente: number): Observable<Array<Reservation>> {
    return this.http.get<{data: Array<Reservation>}>(`${this.apiUrl}/reservacion/public/getReservacionesByCliente/${idCliente}`).pipe(
        map(response => response.data)
    );
  }

  completarReservacion(idReservacion: number): Observable<any> {
    return this.http.post<{data: any}>(`${this.apiUrl}/reservacion/private/completarReservacion?idReservacion=${idReservacion}`, {}).pipe(
        map(response => response.data)
    );
  }

  cancelarReservacion(payload: any): Observable<string> {
    return this.http.post<{data: string}>(`${this.apiUrl}/reservacion/public/cancelarReservacion`, payload).pipe(
        map(response => response.data)
    );
  }
}