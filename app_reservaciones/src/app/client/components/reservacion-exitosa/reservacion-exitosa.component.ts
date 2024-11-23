import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReservacionService } from '../../../core/services/reservacion.service';

@Component({
  selector: 'app-reservacion-exitosa',
  templateUrl: './reservacion-exitosa.component.html',
  styleUrl: './reservacion-exitosa.component.css'
})
export class ReservacionExitosaComponent implements OnInit{
    id: string | null = null
    comprobanteLink: string | null = null

    constructor(private route: ActivatedRoute, private reservacionService: ReservacionService) {}

    ngOnInit(): void {
        this.id = this.route.snapshot.paramMap.get('id');
        if (this.id !== null) {
            this.comprobanteLink = this.reservacionService.getLinkComprobanteReservacion(Number.parseInt(this.id))
        }
    }
}
