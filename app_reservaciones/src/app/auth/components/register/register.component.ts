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

  // Verifica que los campos obligatorios no estén vacíos
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

  onSubmit() {
    if (!this.allFieldsCompleted()) {
      this.authService.openConfirmationDialog(
        'Campos Incompletos',
        'Por favor, completa todos los campos obligatorios.',
        'red'
      );
      return;
    }

    if (this.registerData.password !== this.registerData.confirmPassword) {
      this.authService.openConfirmationDialog(
        'Contraseñas No Coinciden',
        'La confirmación de la contraseña debe coincidir con la contraseña.',
        'red'
      );
      return;
    }

    const { confirmPassword, ...dataToSubmit } = this.registerData;

    this.authService.register(dataToSubmit).subscribe({
      next: () => {
        this.authService.openConfirmationDialog(
          'Registro Exitoso',
          'Se ha enviado un correo de confirmación. Por favor, verifica tu cuenta.',
          'green'
        );
        this.router.navigate(['/auth/login']);
      },
      error: () => {
        this.authService.openConfirmationDialog(
          'Error en el Registro',
          'Hubo un problema con el registro. Por favor, intenta de nuevo.',
          'red'
        );
      },
    });
  }
}
