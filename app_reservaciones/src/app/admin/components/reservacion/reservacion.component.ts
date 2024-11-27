import { Component, OnInit } from '@angular/core';
import { Reservation } from '../../../models/Reservation';
import { Cancelation } from '../../../models/Cancelation';
import { ActivatedRoute } from '@angular/router';
import { ReservacionService } from '../../../core/services/reservacion.service';

@Component({
  selector: 'app-reservacion',
  templateUrl: './reservacion.component.html',
  styleUrl: './reservacion.component.css'
})
export class ReservacionComponent implements OnInit {
    id: number | null = null
    reservacion: Reservation | null = null
    cancelacion: Cancelation | null = null
    comprobanteLink: string | null = null
    facturaLink: string | null = null

    constructor(private route: ActivatedRoute, private reservacionService: ReservacionService) {}

    ngOnInit(): void {
        this.id = Number.parseInt(this.route.snapshot.paramMap.get('id')!);
        if (this.id !== null) {
            this.reservacionService.getReservacion(this.id).subscribe({
                next: (response) => {
                    this.reservacion = response

                    if (this.reservacion.estadoReservacion.nombre === 'Cancelada') {
                        this.reservacionService.getCancelacion(this.id!).subscribe({
                            next: (response) => {
                                this.cancelacion = response
                                this.facturaLink = this.reservacionService.getLinkFacturaCancelacion(this.id!)
                            },
                            error: (error) => {
                                console.log("ERROR al buscar cancelacion")
                                console.log(error)
                            }
                        })
                    } else if (this.reservacion.estadoReservacion.nombre === 'Completada') {
                        this.facturaLink = this.reservacionService.getLinkFactura(this.id!)
                    }

                    this.comprobanteLink = this.reservacionService.getLinkComprobanteReservacion(this.id!)
                },
                error: (error) => {
                    console.log("ERROR al obtener la informaicon de la reservacion")
                    console.log(error)
                    this.reservacion = null
                }
            })
        }
    }

    getSeverity () {
        if (this.reservacion === null) {
            return undefined
        }

        switch (this.reservacion.estadoReservacion.nombre) {
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
