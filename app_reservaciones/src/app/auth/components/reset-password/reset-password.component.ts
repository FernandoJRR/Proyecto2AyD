import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';  // Servicio de autenticación que utilizará el endpoint
import { GlobalService } from '../../../core/services/global.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordData = { email: '' };
  errorMessage = '';
  successMessage = '';

  constructor(private globalService: GlobalService, private authService: AuthService, private router: Router) { }

  configData = { siteName: "Booking App", logoUrl: ""};

  ngOnInit(): void {
      this.globalService.getConfig().subscribe((result) => {
        this.configData = { siteName: result.data.nombre, logoUrl: result.data.imagenString };
      })
  }

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
