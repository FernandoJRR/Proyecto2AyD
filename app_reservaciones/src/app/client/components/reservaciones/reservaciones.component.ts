import { Component, OnInit } from '@angular/core';
import { ReservacionService } from '../../../core/services/reservacion.service';
import { UserStorageService } from '../../../storages/user-storage.service';
import { Reservation } from '../../../models/Reservation';

@Component({
  selector: 'app-reservaciones',
  templateUrl: './reservaciones.component.html',
  styleUrl: './reservaciones.component.css'
})
export class ReservacionesComponent implements OnInit {
    reservaciones: Array<Reservation> = []
    constructor(
        private reservacionService: ReservacionService,
        private userStorage: UserStorageService
    ) {}

    ngOnInit(): void {
        const usuario = this.userStorage.getUser()!
        const idUsuario = usuario.id

        this.reservacionService.getReservacionesByCliente(idUsuario).subscribe({
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
