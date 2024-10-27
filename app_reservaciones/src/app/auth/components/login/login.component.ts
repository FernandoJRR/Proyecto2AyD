import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Role } from '../../../models/Role';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  loginData = {
    email: '',
    password: '',
  };
  hidePassword = true;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    console.log('Iniciando proceso de login');
    console.log('Datos enviados para login:', this.loginData);

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        console.log('Respuesta de la API de login:', response);

        // Acceso correcto a response.data.usuario y response.data.jwt
        if (response?.data?.usuario) {
          const usuario = response.data.usuario;

          console.log('Usuario recibido:', usuario);

          // Verificación de estado de verificación
          if (!usuario.verificado) {
            console.log('Usuario no verificado');
            this.authService.openConfirmationDialog(
              'Cuenta no verificada',
              'Por favor, verifica tu cuenta antes de iniciar sesión.',
              'red'
            );
            return;
          }

          // Verificación de autenticación de dos factores
          if (usuario.twoFactorEnabled) {
            console.log('2FA activado, redirigiendo a /auth/two-factor');
            this.authService.setTempUserData(usuario, response.data.jwt); // Guardar usuario temporal para 2FA
            this.router.navigate(['/auth/two-factor']);
            return;
          }

          // Verificar si el usuario tiene rol de ADMIN
          const isAdmin = usuario.roles.some(
            (role: Role) => role.nombre === 'ADMIN'
          );
          console.log('¿Es Admin?', isAdmin);

          // Redirigir según el rol
          if (isAdmin) {
            console.log('Redirigiendo a /admin');
            this.router.navigate(['/admin']);
          } else {
            console.log('Redirigiendo a /user');
            this.router.navigate(['/user']);
          }
        } else {
          // Si las credenciales son incorrectas
          console.log('Credenciales inválidas');
          this.authService.openConfirmationDialog(
            'Credenciales inválidas',
            'El correo o la contraseña no son correctos.',
            'red'
          );
        }
      },
      error: (error) => {
        console.error('Error en la API de login:', error);
        this.authService.openConfirmationDialog(
          'Error',
          'Ha ocurrido un error en el inicio de sesión.',
          'red'
        );
      },
    });
  }
}
