import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceService } from '../../services/service.service';
import { Service } from '../../../models/Service';
import { Day } from '../../../models/Day';
import { Resource } from '../../../models/Resource';
import { ServiceSchedule } from '../../../models/ServiceSchedule';
import { BusinessService } from '../../services/business.service';

@Component({
  selector: 'app-service-create',
  templateUrl: './service-create.component.html',
  styleUrls: ['./service-create.component.css'],
})
export class ServiceCreateComponent implements OnInit {
  // Formularios
  basicInfoForm: FormGroup;
  detailsForm: FormGroup;

  // Arreglo de horarios de atención
  horariosAtencion: ServiceSchedule[] = [];

  // Listas y banderas
  tiposServicio: any[] = [];
  negocios: any[] = [];
  recursos: Resource[] = [];
  diasAtencion: Day[] = [];
  showResourceSelect: boolean = false;

  constructor(
    private fb: FormBuilder,
    private serviceService: ServiceService,
    private businessService: BusinessService
  ) {
    this.basicInfoForm = this.fb.group({
      nombre: ['', Validators.required],
      tipoServicio: ['', Validators.required],
      negocio: ['', Validators.required],
      recurso: [''],
    });

    this.detailsForm = this.fb.group({
      horas: [0, Validators.required],
      minutos: [0, Validators.required],
      costo: [0, Validators.required],
      dias_cancelacion: [0, Validators.required],
      porcentaje_reembolso: [0, Validators.required],
      trabajadores_simultaneos: [1, Validators.required],
      asignacion_automatica: [false, Validators.required], // Campo booleano para asignación automática
    });
  }

  ngOnInit(): void {
    this.loadServiceTypes();
    this.loadResources();
    this.loadDays();
    this.loadNegocios();
  }

  // Carga de datos desde el servicio
  loadServiceTypes(): void {
    this.serviceService.getServiceTypes().subscribe((data) => {
      this.tiposServicio = data;
      console.log('Tipos de servicio cargados:', this.tiposServicio);
    });
  }

  loadResources(): void {
    this.serviceService.getResources().subscribe((data) => {
      this.recursos = data;
      console.log('Recursos cargados:', this.recursos);
    });
  }

  loadDays(): void {
    this.serviceService.getDays().subscribe((data) => {
      this.diasAtencion = data;
      console.log('Días cargados:', this.diasAtencion);
    });
  }

  loadNegocios(): void {
    this.businessService.getAllBusinesses().subscribe((data) => {
      this.negocios = data;
      console.log('Negocios cargados:', this.negocios);
    });
  }

  // Agregar horario al arreglo de horarios de atención
  addHorarioAtencion(): void {
    const nuevoHorario: ServiceSchedule = {
      horaInicio: '',
      horaFinal: '',
      diaAtencion: { id: 0, nombre: '' },
    };
    this.horariosAtencion.push(nuevoHorario);
    console.log('Horario agregado:', nuevoHorario);
  }

  removeHorarioAtencion(index: number): void {
    this.horariosAtencion.splice(index, 1);
    console.log('Horario eliminado. Horarios actuales:', this.horariosAtencion);
  }

  onTipoServicioChange(): void {
    const tipoServicioId = this.basicInfoForm.get('tipoServicio')?.value;
    this.showResourceSelect = this.tiposServicio.some(
      (tipo) => tipo.id === tipoServicioId && tipo.nombre === 'Recurso'
    );
  }

  onSubmit(): void {
    if (
      this.basicInfoForm.valid &&
      this.detailsForm.valid &&
      this.horariosAtencion.length > 0
    ) {
      const serviceData = {
        servicio: {
          nombre: this.basicInfoForm.get('nombre')?.value,
          tipoServicio: { id: this.basicInfoForm.get('tipoServicio')?.value },
          negocio: { id: this.basicInfoForm.get('negocio')?.value },
          recurso: this.showResourceSelect
            ? { id: this.basicInfoForm.get('recurso')?.value }
            : null,
        },
        duracionServicio: {
          horas: this.detailsForm.get('horas')?.value,
          minutos: this.detailsForm.get('minutos')?.value,
        },
        horariosAtencionServicio: this.horariosAtencion.map((horario) => ({
          horaInicio: horario.horaInicio,
          horaFinal: horario.horaFinal,
          diaAtencion: { id: horario.diaAtencion.id },
        })),
        asignacion_automatica: this.detailsForm.get('asignacion_automatica')
          ?.value,
        costo: this.detailsForm.get('costo')?.value,
        dias_cancelacion: this.detailsForm.get('dias_cancelacion')?.value,
        porcentaje_reembolso: this.detailsForm.get('porcentaje_reembolso')
          ?.value,
        trabajadores_simultaneos: this.detailsForm.get(
          'trabajadores_simultaneos'
        )?.value,
      };

      console.log('Objeto Service construido:', serviceData);

      this.serviceService.createService(serviceData).subscribe({
        next: (response) => {
          console.log('Respuesta de la API al crear servicio:', response);
          alert('Servicio creado exitosamente');
        },
        error: (error) => {
          console.error('Error al crear el servicio:', error);
        },
      });
    } else {
      console.log('Formulario inválido o falta agregar horarios de atención');
    }
  }
}
