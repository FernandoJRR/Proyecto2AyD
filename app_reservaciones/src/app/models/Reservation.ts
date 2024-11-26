import { ReservationState } from "./ReservationState";
import { Service } from "./Service";
import { User } from "./User";

export interface Reservation {
    id: number,
    fecha: string,
    horaInicio: string,
    horaFinal: string,
    usuario: User,
    encargado: User,
    servicio: Service,
    pago: any,
    estadoReservacion: ReservationState,
    createdAt: Date
}