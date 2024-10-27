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
    telefono: '',
    password: '',
    confirmPassword: '',
  };
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.allFieldsCompleted()) {
      this.authService.openConfirmationDialog(
        'Error en el Registro',
        'Por favor, complete todos los campos.',
        'red'
      );
      return;
    }

    if (this.registerData.password !== this.registerData.confirmPassword) {
      this.authService.openConfirmationDialog(
        'Error en el Registro',
        'Las contraseñas no coinciden.',
        'red'
      );
      return;
    }

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        if (response.code === 200) {
          this.authService.openConfirmationDialog(
            response.message,
            'Se ha enviado un correo de confirmación. Por favor, verifica tu cuenta.',
            'green'
          );
          this.router.navigate(['/auth/login']);
        }
      },
      error: (error) => {
        const errorMsg = error.error?.message || 'Error en el registro';
        const errorDescription = error.error?.error || 'Hubo un problema al procesar la solicitud.';

        this.authService.openConfirmationDialog(
          errorMsg,
          errorDescription,
          'red'
        );
      },
    });
  }

  allFieldsCompleted(): boolean {
    return (
      this.registerData.nombres !== '' &&
      this.registerData.apellidos !== '' &&
      this.registerData.email !== '' &&
      this.registerData.nit !== '' &&
      this.registerData.cui !== '' &&
      this.registerData.telefono !== '' &&
      this.registerData.password !== '' &&
      this.registerData.confirmPassword !== ''
    );
  }
}
