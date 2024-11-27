import { Component, OnInit } from '@angular/core';
import { ReporteService } from '../../../core/services/reporte.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
    tipoTiempo: any[] | undefined;
    estadoReservacion: any[] | undefined;
    cantidadTiempo: number = 1

    selectedTipoTiempo: any | undefined;
    selectedEstado: any | undefined;

    cantidad = 0

    constructor(
        private reportService: ReporteService
    ){}

    ngOnInit() {
        this.tipoTiempo = [
            { name: 'Semana', code: 'semana' },
            { name: 'Mes', code: 'mes' },
            { name: 'Año', code: 'anio' }
        ];
        this.estadoReservacion = [
            { name: 'Programada', code: 'programada' },
            { name: 'Completada', code: 'completada' },
            { name: 'Cancelada', code: 'cancelada' }
        ];
        this.selectedTipoTiempo = this.tipoTiempo[0]
        this.fetchConteoReservaciones()
    }

    fetchConteoReservaciones() {
        this.reportService.getConteoReport(this.selectedTipoTiempo.code, this.cantidad, this.selectedEstado === undefined ? null : this.selectedEstado.code).subscribe({
            next: (response) => {
                this.cantidad = response.data
            },
            error: (error) => {
                this.cantidad = 0
            }
        })
    }
}
