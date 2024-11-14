import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-password-recovery',
  templateUrl: './password-recovery.component.html',
  styleUrls: ['./password-recovery.component.css'],
})
export class PasswordRecoveryComponent implements OnInit {
  verificationCode: string = '';

  newPasswordForm: FormGroup;
  password = '';
  passwordAgain = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    fb: FormBuilder
  ) {
    this.newPasswordForm = fb.group({
        password: ["", Validators.required],
        passwordAgain: ["", Validators.required]
    });
  }

  ngOnInit(): void {
    // Extrae el código de verificación de la URL y lo imprime
    this.verificationCode = this.route.snapshot.queryParamMap.get('c') || '';
    console.log('Código de verificación extraído de la URL:', this.verificationCode);
  }

  onSubmit(): void {
    if (this.verificationCode) {
      if (!this.newPasswordForm.valid) {
          this.newPasswordForm.markAllAsTouched()
          return
      }

      const verificationPayload = { codigoVerificacion: this.verificationCode };
      // Imprime el JSON que se enviará
      console.log('Datos enviados al endpoint de verificación:', JSON.stringify(verificationPayload));

      // Envía la solicitud de verificación usando PATCH
      this.authService.recoverPassword(this.password, this.verificationCode).subscribe({
        next: (response) => {
          console.log('Respuesta de la API:', response); // Imprime la respuesta de la API
          if (response.code === 200) {
            this.authService.openConfirmationDialog(
              response.message || 'Recuperacion Exitosa',
              response.data || 'Su contraseña ha sido recuperada exitosamente. Ahora puede iniciar sesión.',
              'green'
            );
            this.router.navigate(['/auth/login']);
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
    } else {
      this.authService.openConfirmationDialog(
        'Código Inválido',
        'El link de verificacion no es correcto.',
        'red'
      );
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.newPasswordForm.get(fieldName);
    return control! && control.invalid && (control.dirty || control.touched);
  }
}
