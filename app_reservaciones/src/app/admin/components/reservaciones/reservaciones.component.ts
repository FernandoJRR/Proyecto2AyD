import { Component, OnInit } from '@angular/core';
import { Reservation } from '../../../models/Reservation';
import { ReservacionService } from '../../../core/services/reservacion.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-reservaciones',
  templateUrl: './reservaciones.component.html',
  styleUrl: './reservaciones.component.css'
})
export class ReservacionesComponent implements OnInit {
    reservaciones: Array<Reservation> = []
    constructor(
        private reservacionService: ReservacionService,
        private authService: AuthService,
    ) {}

    ngOnInit(): void {
        this.loadReservaciones()
    }

    loadReservaciones() {
        this.reservacionService.getAllReservacion().subscribe({
            next: (response) => {
                this.reservaciones = response
            },
            error: (error) => {
                console.log("ERROR al obtener las reservaciones")
                console.log(error)
                this.reservaciones = []
            }
        })
    }

    completarReservacion(idReservacion: number) {
        this.reservacionService.completarReservacion(idReservacion).subscribe({
            next: (response) => {
                this.authService.openConfirmationDialog('Completacion Exitosa', 'Reservacion completada exitosamente y factura generada!', 'green')
                this.loadReservaciones()
            },
            error: (error) => {
                console.log("ERROR al completar la reservacion")
                console.log(error)
            }
        })
    }

    getSeverity (reservacion: Reservation) {
        switch (reservacion.estadoReservacion.nombre) {
            case 'Programada':
                return 'info';

            case 'Cancelada':
                return 'danger';

            case 'Completada':
                return 'success';

            default:
                return undefined;
        }
    };
}
