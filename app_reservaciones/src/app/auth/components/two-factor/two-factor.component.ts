import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-two-factor',
  templateUrl: './two-factor.component.html',
  styleUrl: './two-factor.component.css'
})
export class TwoFactorComponent {

    codigoForm: FormGroup;

    codigo = ''

    constructor (
        private authService: AuthService,
        private router: Router,
        fb: FormBuilder
    ) {
        this.codigoForm = fb.group({
            codigo: ["", Validators.required],
        });
    }

  onSubmit(): void {
      if (!this.codigoForm.valid) {
          this.codigoForm.markAllAsTouched()
          return
      }

      // Envía la solicitud de verificación usando PATCH
      this.authService.login2F(this.codigo).subscribe({
        next: (response) => {
          console.log('Respuesta de la API:', response); // Imprime la respuesta de la API
          if (response.code === 200) {
            this.router.navigate(['/client']);
          }
        },
        error: (error) => {
          console.error('Error en la recuperacion:', error); // Imprime el error de la API
          const errorMsg = error.error?.message || 'Error en Recuperacion';
          const errorDescription = error.error?.error || 'Ha ocurrido un error en la recuperacion.';
          this.authService.openConfirmationDialog(
            errorMsg,
            errorDescription,
            'red'
          );
        }
      });
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.codigoForm.get(fieldName);
    return control! && control.invalid && (control.dirty || control.touched);
  }
}
