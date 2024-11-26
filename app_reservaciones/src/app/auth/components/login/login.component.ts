import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserRole } from '../../../models/UserRole';
import { User } from '../../../models/User';
import { GlobalService } from '../../../core/services/global.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  loginData = {
    email: '',
    password: '',
  };

  hidePassword = true;

  constructor(private globalService: GlobalService, private authService: AuthService, private router: Router) {}

  configData = { siteName: "Booking App", logoUrl: ""};

  ngOnInit(): void {
      this.globalService.getConfig().subscribe((result) => {
        this.configData = { siteName: result.data.nombre, logoUrl: result.data.imagenString };
      })
  }

  onSubmit() {
    if (!this.loginData.email || !this.loginData.password) {
      this.authService.openConfirmationDialog(
        'Campos Requeridos',
        'Debes completar ambos campos para iniciar sesión.',
        'red'
      );
      return;
    }

    console.log('Iniciando proceso de login');
    console.log('Datos capturados del login:', this.loginData);

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        console.log('Respuesta de la API de login:', response);

        if (response?.data?.usuario) {
          const usuario: User = response.data.usuario; // Aplica el tipo User

          //Probar con validated la activacion tambien!
          usuario.verificado = response.data.validated; // Asegura que esté en `verificado`

          usuario.twoFactorEnabled = response.data.hasTwoFactorCode;

          console.log('Usuario extraído de la respuesta de la API:', usuario);

          if (!usuario.verificado) {
            this.authService.openConfirmationDialog(
              'Cuenta no verificada',
              'Por favor, verifica tu cuenta antes de iniciar sesión.',
              'red'
            );
            return;
          }

          if (usuario.twoFactorEnabled) {
            this.authService.setTempUserData(usuario, response.data.jwt);
            this.router.navigate(['/auth/two-factor']);
            return;
          }

          // Verificación del rol "ADMIN" accediendo correctamente a la propiedad `rol.nombre`
          const isAdmin = usuario.roles.some(
            (userRole: UserRole) => userRole.rol.nombre === 'ADMIN'
          );
          if (isAdmin) {
            console.log('Redirigiendo a la ruta /admin');
            this.router.navigate(['/admin/dashboard']);
          } else {
            console.log('Redirigiendo a la ruta /client');
            this.router.navigate(['/client']);
          }
        } else {
          this.authService.openConfirmationDialog(
            'Credenciales incorrectas',
            'El correo o la contraseña no son correctos.',
            'red'
          );
        }
      },
      error: (error) => {
        if (error.status === 400) {
          this.authService.openConfirmationDialog(
            'Credenciales incorrectas',
            'El correo o la contraseña no son correctos.',
            'red'
          );
        } else {
          this.authService.openConfirmationDialog(
            'Error',
            'Ha ocurrido un error al intentar iniciar sesión.',
            'red'
          );
        }
      },
    });
  }
}
