import {
  Component,
  ChangeDetectionStrategy,
  EventEmitter,
  OnInit,
  ChangeDetectorRef,
  ViewEncapsulation,
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import {
  CalendarDayViewBeforeRenderEvent,
  CalendarEvent,
  CalendarEventTimesChangedEvent,
  CalendarMonthViewBeforeRenderEvent,
  CalendarView,
} from 'angular-calendar';
import { Router } from '@angular/router';
import { addHours, addMinutes, setHours, setMinutes } from 'date-fns';
import { Subject } from 'rxjs';
import { UserStorageService } from '../../../storages/user-storage.service';
import { ReservacionService } from '../../../core/services/reservacion.service';
import { BusinessService } from '../../services/business.service';
import { ServiceService } from '../../services/service.service';
import { AuthService } from '../../../auth/services/auth.service';
import { Service } from '../../../models/Service';

@Component({
  selector: 'app-nueva-reservacion',
  templateUrl: './nueva-reservacion.component.html',
  styleUrl: './nueva-reservacion.component.css',
  encapsulation: ViewEncapsulation.None,
  //changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NuevaReservacionComponent implements OnInit {
  view: CalendarView = CalendarView.Month;

  viewDate: Date = new Date();

  viewDateDay: Date = new Date();

  events: CalendarEvent[] = [];

  viewDateChange = new EventEmitter<Date>();

  clickedColumn = 1;

  nuevaReservacionForm: FormGroup;

  refresh = new Subject<void>();

  duracionString = '';

  userEmail = '';

  negocios: Array<any> = [];
  servicios: Array<any> = [];
  encargados: Array<any> = [];
  unidadesRecurso: Array<any> = [];
  metodosPago: Array<any> = [];

  constructor(
    private userStorage: UserStorageService,
    private router: Router,
    private authService: AuthService,
    private businessService: BusinessService,
    private serviceService: ServiceService,
    private reservacionService: ReservacionService,
    //private cd: ChangeDetectorRef,
    fb: FormBuilder
  ) {
    this.userEmail = userStorage.getUser()!.email;
    this.nuevaReservacionForm = fb.group({
      negocio: new FormControl('', [Validators.required]),
      servicio: new FormControl('', [Validators.required]),
      encargado: new FormControl('', [this.encargadoValidator.bind(this)]),
      unidadRecurso: new FormControl('', [
        this.unidadRecursoValidator.bind(this),
      ]),
      fecha: new FormControl(new Date(), [
        Validators.required,
        this.fechaValidator.bind(this),
      ]),
      horaInicio: new FormControl(12, [Validators.required, this.horaValidator.bind(this)]),
      minutoInicio: new FormControl(0, [Validators.required]),
      horaFin: new FormControl(12, [Validators.required]),
      minutoFin: new FormControl(30, [Validators.required]),
      metodoPago: new FormControl('', [Validators.required]),
      numeroPago: new FormControl('', [
        Validators.required,
        Validators.min(0),
        Validators.pattern('^[0-9]*$'),
      ]),
    });

    this.nuevaReservacionForm.get('servicio')?.valueChanges.subscribe(() => {
      this.nuevaReservacionForm.get('unidadRecurso')?.updateValueAndValidity();
      this.nuevaReservacionForm.get('encargado')?.updateValueAndValidity();
    });

    this.nuevaReservacionForm.get('encargado')?.valueChanges.subscribe(() => {
      this.nuevaReservacionForm.get('encargado')?.updateValueAndValidity();
    });

    this.nuevaReservacionForm
      .get('unidadRecurso')
      ?.valueChanges.subscribe(() => {
        this.nuevaReservacionForm
          .get('unidadRecurso')
          ?.updateValueAndValidity();
      });

    this.nuevaReservacionForm.get('horaInicio')?.valueChanges.subscribe(() => {
      this.updateTime();
    });

    this.nuevaReservacionForm.get('fecha')!.valueChanges.subscribe(() => {
        this.nuevaReservacionForm.get('horaInicio')!.updateValueAndValidity()
    })

    this.nuevaReservacionForm
      .get('minutoInicio')
      ?.valueChanges.subscribe(() => {
        this.updateTime();
      });
  }

  ngOnInit(): void {
    this.businessService.getAllBusinesses().subscribe({
      next: (response) => {
        this.negocios = response;
        //this.cd.detectChanges()
      },
      error: (error) => {
        console.log('ERROR al obtener negocios');
        console.log(error);
        this.negocios = [];
      },
    });

    this.reservacionService.getMetodosPago().subscribe({
      next: (response) => {
        this.metodosPago = response;
      },
      error: (error) => {
        console.log('ERROR al obtener metodos de pago');
        console.log(error);
        this.metodosPago = [];
      },
    });
  }

  horarioDia: any = null;

  diaSemanaMap: Map<number, string> = new Map<number, string>([
    [0, 'Domingo'],
    [1, 'Lunes'],
    [2, 'Martes'],
    [3, 'Miercoles'],
    [4, 'Jueves'],
    [5, 'Viernes'],
    [6, 'Sabado'],
  ]);

  disponibilidadFechaServicio(fecha: Date, servicio: Service) {
    const horariosAtencionServicio = servicio.horariosAtencionServicios;
    const diaSemanaFecha = this.diaSemanaMap.get(fecha.getDay());

    if (diaSemanaFecha === undefined) {
      //Si no se encuentra el dia de la semana se elimina
      return false;
    }

    return horariosAtencionServicio.some(
      (schedule) => schedule.diaAtencion.nombre === diaSemanaFecha
    );
  }

  disponibilidadHoraServicio(fecha: Date, hora: string, servicio: Service) {
    // Obtiene el nombre del día (por ejemplo, 'Lunes') de la fecha dada
    const dayName = this.diaSemanaMap.get(fecha.getDay());

    // Filtra los horarios del día
    const daySchedules = servicio.horariosAtencionServicios.filter(
      (schedule) => schedule.diaAtencion.nombre === dayName
    );

    if (daySchedules.length === 0) {
      // Si no hay horarios para ese día
      return false;
    }

    // Convierte la hora dada a un número en minutos para facilitar la comparación
    const [givenHours, givenMinutes] = hora.split(':').map(Number);
    const givenTimeInMinutes = givenHours * 60 + givenMinutes;

    // Verifica si la hora cae dentro de alguno de los rangos del día
    return daySchedules.some((schedule) => {
      const [startHours, startMinutes] = schedule.horaInicio
        .split(':')
        .map(Number);
      const startTimeInMinutes = startHours * 60 + startMinutes;

      const [endHours, endMinutes] = schedule.horaFinal.split(':').map(Number);
      const endTimeInMinutes = endHours * 60 + endMinutes;

      return (
        givenTimeInMinutes >= startTimeInMinutes &&
        givenTimeInMinutes <= endTimeInMinutes
      );
    });
  }

  getHorarioDia(fecha: Date, servicio: Service) {
    const dayName = this.diaSemanaMap.get(fecha.getDay());

    const daySchedules = servicio.horariosAtencionServicios.filter(
      (schedule) => schedule.diaAtencion.nombre === dayName
    );

    if (daySchedules.length === 0) {
      return null;
    }

    return daySchedules[0];
  }

  selectDay(date: Date) {
    if (
      this.disponibilidadFechaServicio(
        date,
        this.nuevaReservacionForm.get('servicio')!.value
      )
    ) {
      this.nuevaReservacionForm.get('fecha')!.setValue(date);
      this.horarioDia = this.getHorarioDia(
        date,
        this.nuevaReservacionForm.get('servicio')!.value
      );
    }
  }

  beforeDayViewRender(renderEvent: CalendarDayViewBeforeRenderEvent) {
    renderEvent.hourColumns.forEach((hourColumn) => {
      hourColumn.hours.forEach((hour) => {
        hour.segments.forEach((segment) => {
          const hora = segment.date.toLocaleTimeString();
          const fecha = this.nuevaReservacionForm.get('fecha')!.value;
          const servicio = this.nuevaReservacionForm.get('servicio')!.value;
          const disponibilidad = this.disponibilidadHoraServicio(
            fecha,
            hora,
            servicio
          );
          if (!disponibilidad) {
            segment.cssClass = 'disabled-segment';
          }
        });
      });
    });
  }

  negocioUpdate() {
    console.log('nuevo');
    this.nuevaReservacionForm.get('servicio')!.setValue(null);
    this.fetchServicios();
  }

  fetchServicios() {
    if (this.nuevaReservacionForm.get('negocio')!.value === null) {
      this.servicios = [];
    } else {
      this.serviceService
        .getServicesByBusinessId(
          this.nuevaReservacionForm.get('negocio')!.value.id
        )
        .subscribe({
          next: (response) => {
            this.servicios = response;
          },
          error: (error) => {
            console.log('ERROR al obtener servicios');
            console.log(error);
            this.servicios = [];
          },
        });
    }
  }

  fetchEncargados() {
    if (this.nuevaReservacionForm.get('servicio')!.value === null) {
      this.encargados = [];
    } else {
      this.serviceService
        .getEncargadosByService(
          this.nuevaReservacionForm.get('servicio')!.value.id
        )
        .subscribe({
          next: (response) => {
            this.encargados = response;
          },
          error: (error) => {
            console.log('ERROR al obtener encargados');
            console.log(error);
            this.encargados = [];
          },
        });
    }
  }

  fetchUnidadesRecurso() {
    if (this.nuevaReservacionForm.get('servicio')!.value === null) {
      this.unidadesRecurso = [];
    } else {
      this.serviceService
        .getUnidadesRecursoByService(
          this.nuevaReservacionForm.get('servicio')!.value.id
        )
        .subscribe({
          next: (response) => {
            this.unidadesRecurso = response;
          },
          error: (error) => {
            console.log('ERROR al obtener unidades de recurso');
            console.log(error);
            this.unidadesRecurso = [];
          },
        });
    }
  }

  formatDuracion() {
    const horas =
      this.nuevaReservacionForm.get('servicio')!.value.duracionServicio.horas;
    const minutos =
      this.nuevaReservacionForm.get('servicio')!.value.duracionServicio.minutos;
    return (
      (horas === 0 ? '' : horas + ' Hora/s') +
      (minutos === 0 ? '' : (horas === 0 ? '' : ' ') + minutos + ' Minuto/s')
    );
  }

  formatTime(horas: string, minutos: string) {
    const horasInt = Number.parseInt(horas);
    const minutosInt = Number.parseInt(minutos);

    const horasStr =
      horasInt < 10 ? '0' + horasInt.toString() : horasInt.toString();
    const minutosStr =
      minutosInt < 10 ? '0' + minutosInt.toString() : minutosInt.toString();

    return horasStr + ':' + minutosStr;
  }

  serviceChange() {
    this.nuevaReservacionForm.get('unidadRecurso')!.reset();
    if (this.nuevaReservacionForm.get('servicio')!.value === null) {
      this.duracionString = '';
      this.events = [];
      this.horarioDia = null;
      return;
    }
    this.fetchEncargados();
    this.fetchUnidadesRecurso();
    this.horarioDia = this.getHorarioDia(
      new Date(),
      this.nuevaReservacionForm.get('servicio')!.value
    );
    this.duracionString = this.formatDuracion();
    const inicio = setMinutes(
      setHours(new Date(), this.nuevaReservacionForm.get('horaInicio')!.value),
      this.nuevaReservacionForm.get('minutoInicio')!.value
    );
    const final = addMinutes(
      addHours(
        inicio,
        this.nuevaReservacionForm.get('servicio')!.value.duracionServicio.horas
      ),
      this.nuevaReservacionForm.get('servicio')!.value.duracionServicio.minutos
    );
    const horaFin = Number.parseInt(
      final.toLocaleTimeString('es-ES', { hour: '2-digit' })
    );
    const minutoFin = Number.parseInt(
      final.toLocaleTimeString('es-ES', { minute: '2-digit' })
    );

    this.nuevaReservacionForm.get('horaFin')!.setValue(horaFin);
    this.nuevaReservacionForm.get('minutoFin')!.setValue(minutoFin);

    this.events = [
      {
        title: 'Cita',
        color: {
          primary: '#e3bc08',
          secondary: '#FDF1BA',
        },
        start: inicio,
        end: final,
        draggable: true,
      },
    ];
  }

  encargadoValidator(control: AbstractControl) {
    const servicio = this.nuevaReservacionForm?.get('servicio')?.value;

    // Si `servicio` no está definido, el validador se pasa sin errores
    if (!servicio) {
      return null;
    }

    const requiereEncargado = !servicio.asignacion_automatica;

    // Validación: Si `requiereEncargado` es true, `encargado` debe tener un valor
    if (requiereEncargado && !control.value) {
      return { required: true };
    }

    // Validación: Si `requiereEncargado` es false, `encargado` debe estar vacío
    if (!requiereEncargado && control.value) {
      return { mustBeEmpty: true };
    }

    // Si todo está bien, retorna null (sin errores)
    return null;
  }

  unidadRecursoValidator(control: AbstractControl) {
    const servicio = this.nuevaReservacionForm?.get('servicio')?.value;

    // Si `servicio` no está definido, el validador se pasa sin errores
    if (!servicio) {
      return null;
    }

    const requiereRecurso = servicio.recurso !== null;

    // Validación: Si `tieneRecurso` es true, `unidadRecurso` debe tener un valor
    if (requiereRecurso && !control.value) {
      return { required: true };
    }

    // Validación: Si `tieneRecurso` es false, `unidadRecurso` debe estar vacío
    if (!requiereRecurso && control.value) {
      return { mustBeEmpty: true };
    }

    // Si todo está bien, retorna null (sin errores)
    return null;
  }

  fechaValidator(control: AbstractControl) {
    if (
      this.nuevaReservacionForm === undefined ||
      this.nuevaReservacionForm.get('servicio') === undefined
    ) {
      return null;
    } else {
      const servicio = this.nuevaReservacionForm?.get('servicio')!.value;

      if (!control.value) {
        return null;
      }

      if (!this.disponibilidadFechaServicio(control.value, servicio)) {
        return { invalid: true };
      }

      return null;
    }
  }

  horaValidator(control: AbstractControl) {
    if (
      this.nuevaReservacionForm === undefined ||
      this.nuevaReservacionForm.get('servicio') === undefined
    ) {
      return null;
    } else {
      const servicio = this.nuevaReservacionForm?.get('servicio')!.value;
      const fecha = this.nuevaReservacionForm?.get('fecha')!.value;

      if (!fecha) {
          return null
      }

      if (!control.value) {
        return null;
      }

      const horaStr = control.value + ':' + this.nuevaReservacionForm.get('minutoInicio')!.value
      if (!this.disponibilidadHoraServicio(fecha, horaStr, servicio)) {
        return { invalid: true };
      }

      return null;
    }
  }

  updateTime() {
    const horas = this.nuevaReservacionForm.get('horaInicio')!.value;
    const minutos = this.nuevaReservacionForm.get('minutoInicio')!.value;

    const horasDuracion =
      this.nuevaReservacionForm.get('servicio')!.value.duracionServicio.horas;
    const minutosDuracion =
      this.nuevaReservacionForm.get('servicio')!.value.duracionServicio.minutos;

    const start = setMinutes(setHours(new Date(), horas), minutos);
    const end = addMinutes(addHours(start, horasDuracion), minutosDuracion);

    this.nuevaReservacionForm
      .get('horaFin')!
      .setValue(
        Number.parseInt(end.toLocaleTimeString('es-ES', { hour: '2-digit' }))
      );
    this.nuevaReservacionForm
      .get('minutoFin')!
      .setValue(
        Number.parseInt(end.toLocaleTimeString('es-ES', { minute: '2-digit' }))
      );

    this.events = [
      {
        title: 'Cita',
        color: {
          primary: '#e3bc08',
          secondary: '#FDF1BA',
        },
        start: start,
        end: end,
        draggable: true,
      },
    ];

    this.refresh.next();
  }

  crearReservacion() {
    let payload: any = {};
    payload.emailUsuario = this.userEmail;
    payload.idEncargado = this.nuevaReservacionForm.get('encargado')!.value
      ? this.nuevaReservacionForm.get('encargado')!.value.id
      : null;
    payload.idServicio = this.nuevaReservacionForm.get('servicio')!.value.id;

    if (this.nuevaReservacionForm.get('unidadRecurso')!.value) {
      payload.nombreUnidadRecurso =
        this.nuevaReservacionForm.get('unidadRecurso')!.value.nombre;
      payload.nombreRecurso =
        this.nuevaReservacionForm.get('unidadRecurso')!.value.recurso.nombre;
    }

    payload.horaInicio = this.formatTime(
      this.nuevaReservacionForm.get('horaInicio')!.value,
      this.nuevaReservacionForm.get('minutoInicio')!.value
    );
    payload.horaFinal = this.formatTime(
      this.nuevaReservacionForm.get('horaFin')!.value,
      this.nuevaReservacionForm.get('minutoFin')!.value
    );

    payload.fecha = this.nuevaReservacionForm
      .get('fecha')!
      .value.toLocaleDateString('en-CA');
    payload.nombreMetodoPago =
      this.nuevaReservacionForm.get('metodoPago')!.value.nombre;
    payload.numeroPago = this.nuevaReservacionForm.get('numeroPago')!.value;
    payload.montoPago = this.nuevaReservacionForm.get('servicio')!.value.costo;

    console.log('PAYLOAD');
    console.log(payload);
    this.reservacionService.crearReservacion(payload).subscribe({
      next: (response) => {
        this.router.navigate([`/client/reservacion-exitosa/${response.id}`]);
      },
      error: (error) => {
        console.log('ERROR al crear reservacion');
        console.log(error);
        //this.authService.openConfirmationDialog("Error", error, "red")
      },
    });
  }

  eventTimesChanged({
    event,
    newStart,
    newEnd,
  }: CalendarEventTimesChangedEvent): void {
    event.start = newStart;
    event.end = newEnd;

    this.nuevaReservacionForm
      .get('horaInicio')!
      .setValue(
        Number.parseInt(
          newStart.toLocaleTimeString('es-ES', { hour: '2-digit' })
        )
      );
    this.nuevaReservacionForm
      .get('minutoInicio')!
      .setValue(
        Number.parseInt(
          newStart.toLocaleTimeString('es-ES', { minute: '2-digit' })
        )
      );

    this.refresh.next();
    this.viewDateDay.setDate(
      this.nuevaReservacionForm.get('fecha')!.value.getDate()
    );
  }

  //negocios = [{ nombre: 'Barberia' }, { nombre: 'Canchas Deportivas' }];
  /*
  servicios = [
    {
      nombre: 'Corte de Pelo',
      tieneRecurso: false,
      asignacionAutomatica: true,
      duracion: { minutos: 30, horas: 0 },
      costo: 50,
    },
    {
      nombre: 'Renta de Canchas de Futbol',
      tieneRecurso: true,
      asignacionAutomatica: false,
      duracion: { minutos: 0, horas: 1 },
      costo: 130,
    },
  ];
  */
  //encargados = [{ nombre: 'Miguel' }, { nombre: 'Rosa' }];
  //unidadesRecurso = [{ nombre: 'Cancha Mediana' }, { nombre: 'Cancha Grande' }];
  //metodosPago = [{ nombre: 'Transferencia' }, { nombre: 'Tarjeta' }];
}
