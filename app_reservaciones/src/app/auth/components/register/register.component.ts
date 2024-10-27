import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  registerData = {
    nombres: '',
    apellidos: '',
    email: '',
    nit: '',
    cui: '',
    password: '',
    confirmPassword: '',
  };
  hidePassword = true;
  hideConfirmPassword = true;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    // Validación de contraseñas coincidentes
    if (this.registerData.password !== this.registerData.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    // Llama al servicio de registro (authService) con los datos
    this.authService.register(this.registerData).subscribe({
      next: () => {
        // Abre el diálogo de confirmación si el registro es exitoso
        this.authService.openConfirmationDialog(
          'Registro Exitoso',
          'Se ha enviado un correo de confirmación. Por favor, verifica tu cuenta.',
          'light-green'
        );
        // Redirige al login tras el cierre del diálogo
        this.router.navigate(['/auth/login']);
      },
      error: () => {
        // Muestra un mensaje de error si algo sale mal
        this.errorMessage = 'Error en el registro. Inténtalo de nuevo.';
      },
    });
  }
}
