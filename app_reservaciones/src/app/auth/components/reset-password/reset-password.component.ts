import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';  // Servicio de autenticación que utilizará el endpoint

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {
  resetPasswordData = { email: '' };
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.authService.resetPassword(this.resetPasswordData.email).subscribe({
      next: (response) => {
        this.successMessage = 'Te hemos enviado un enlace para restablecer tu contraseña.';
      },
      error: (error) => {
        this.errorMessage = 'Error al intentar enviar el correo. Inténtalo de nuevo.';
      }
    });
  }
}
