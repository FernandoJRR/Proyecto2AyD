import { Component, OnInit } from '@angular/core';
import { Reservation } from '../../../models/Reservation';
import { ActivatedRoute, Router } from '@angular/router';
import { ReservacionService } from '../../../core/services/reservacion.service';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';

@Component({
  selector: 'app-cancelar-reservacion',
  templateUrl: './cancelar-reservacion.component.html',
  styleUrl: './cancelar-reservacion.component.css',
})
export class CancelarReservacionComponent implements OnInit {
  id: string | null = null;
  reservacion: Reservation | null = null;
  comprobanteLink: string | null = null;
  cancelacionForm: FormGroup;
  fechaMin: Date = new Date();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private reservacionService: ReservacionService,
    fb: FormBuilder
  ) {
    this.cancelacionForm = fb.group({
      fechaCancelacion: new FormControl('', [Validators.required]),
      motivo: new FormControl('', [Validators.required]),
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    if (this.id !== null) {
      this.reservacionService
        .getReservacion(Number.parseInt(this.id))
        .subscribe({
          next: (response) => {
            this.reservacion = response;
            let temp: Number[] = this.reservacion.fecha.split('-').map(Number)
            this.fechaMin = new Date(Date.UTC(temp[0] as number, temp[1] as number - 1, temp[2] as number + 1))
            this.comprobanteLink =
              this.reservacionService.getLinkComprobanteReservacion(
                Number.parseInt(this.id!)
              );
          },
          error: (error) => {
            console.log('ERROR al obtener la informaicon de la reservacion');
            console.log(error);
            this.reservacion = null;
          },
        });
    }
  }

  cancelar() {
    if (this.cancelacionForm.invalid) {
      if (this.cancelacionForm.get('fechaCancelacion')!.invalid) {
        alert('Fecha de Cancelacion Invalida');
        return;
      }
      if (this.cancelacionForm.get('motivo')!.invalid) {
        alert('Motivo de Cancelacion Invalido');
        return;
      }
    }

    const fechaCancelacion = (
      this.cancelacionForm.get('fechaCancelacion')!.value as Date
    ).toLocaleDateString('en-CA');

    const payload = {
      idReservacion: this.id,
      motivoCancelacion: this.cancelacionForm.get('motivo')!.value,
      fechaCancelacion: fechaCancelacion,
    };

    this.reservacionService.cancelarReservacion(payload).subscribe({
      next: (response) => {
        alert(response);
        this.router.navigate(['/client/reservaciones']);
      },
      error: (error) => {
        alert('Error al cancelar reservacion');
        console.log('ERROR al cancelar reservacion');
        console.log(error);
      },
    });
  }
}
