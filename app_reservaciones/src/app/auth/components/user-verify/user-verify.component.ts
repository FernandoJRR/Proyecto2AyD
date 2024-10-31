import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-verify',
  templateUrl: './user-verify.component.html',
  styleUrls: ['./user-verify.component.css']
})
export class UserVerifyComponent implements OnInit {
  verificationCode: string = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Extrae el código de verificación de la URL y lo imprime
    this.verificationCode = this.route.snapshot.queryParamMap.get('c') || '';
    console.log('Código de verificación extraído de la URL:', this.verificationCode);
  }

  onVerifyAccount(): void {
    if (this.verificationCode) {
      const verificationPayload = { codigoVerificacion: this.verificationCode };
      // Imprime el JSON que se enviará
      console.log('Datos enviados al endpoint de verificación:', JSON.stringify(verificationPayload));

      // Envía la solicitud de verificación usando PATCH
      this.authService.verifyUser(verificationPayload).subscribe({
        next: (response) => {
          console.log('Respuesta de la API:', response); // Imprime la respuesta de la API
          if (response.code === 200) {
            this.authService.openConfirmationDialog(
              response.message || 'Verificación Exitosa',
              response.data || 'Su cuenta ha sido verificada exitosamente. Ahora puede iniciar sesión.',
              'green'
            );
            this.router.navigate(['/auth/login']);
          }
        },
        error: (error) => {
          console.error('Error en la verificación:', error); // Imprime el error de la API
          const errorMsg = error.error?.message || 'Error de Verificación';
          const errorDescription = error.error?.error || 'Ha ocurrido un error en la verificación.';
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
}
