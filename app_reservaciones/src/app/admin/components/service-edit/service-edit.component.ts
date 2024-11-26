import { Component, OnInit } from '@angular/core';
import { Service } from '../../../models/Service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ServiceService } from '../../services/service.service';
import { DialogComponent } from '../../../utils/dialog/dialog.component';

@Component({
  selector: 'app-service-edit',
  templateUrl: './service-edit.component.html',
  styleUrl: './service-edit.component.css'
})
export class ServiceEditComponent implements OnInit {
  service: any = {
    id: 0,
    nombre: '',
    duracionServicio: {id: 0, minutos: 0, horas: 0},
    horariosAtencionServicios: [{id: 0, horaInicio: '', horaFinal: '', diaAtencion: {id: 0, nombre: ''}}],
    asignacion_automatica: false,
    dias_cancelacion: 0,
    porcentaje_reembolso: 0,
    trabajadores_simultaneos: 0,
  };
  constructor(
    private serviceService: ServiceService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog
  ) {}

  diasSemana = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];

  ngOnInit(): void {
    const serviceId = this.route.snapshot.paramMap.get('id');
    if (serviceId) {
      console.log('ID del negocio a editar:', serviceId);
      this.loadService(Number(serviceId));
    }
  }

  loadService(id: number): void {
    this.serviceService.getService(id).subscribe({
      next: (data: Service) => {
        console.log('Datos del negocio obtenidos de la API:', data);
        this.service = data;
        console.log('Objeto Business asignado:', this.service);
      },
      error: () => {
        this.openDialog('Error', 'Error al cargar los datos del negocio.', 'red');
      }
    });
  }

  onSubmit(): void {
    if (!this.service.nombre) {
      this.openDialog('Campo vacío', 'El nombre del negocio no puede estar vacío.', 'red');
      return;
    }

    this.serviceService.updateService(this.service).subscribe({
      next: () => {
        this.openDialog('Actualización exitosa', 'Negocio actualizado con éxito.', 'green');
        this.router.navigate(['/admin/business']);
      },
      error: () => {
        this.openDialog('Error', 'Error al actualizar el negocio.', 'red');
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/services']);
  }

  addHorario(): void {
    this.service.horariosAtencionServicios.push({
      horaInicio: '',
      horaFinal: '',
      diaAtencion: { nombre: '' },
    });
  }

  removeHorario(index: number): void {
    this.service.horariosAtencionServicios.splice(index, 1);
  }

  openDialog(title: string, description: string, backgroundColor: 'red' | 'green' | 'gray'): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }
}
