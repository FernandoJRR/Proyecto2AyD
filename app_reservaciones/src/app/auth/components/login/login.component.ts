import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';  // El servicio de autenticación que vamos a crear
import { MaterialModule } from '../../../material/material.module';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent {
  loginData = { email: '', password: '' };
  hidePassword = true;  // Variable para controlar la visibilidad de la contraseña
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        // Si la autenticación es exitosa, redirige al dashboard o página principal según el rol
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        // Muestra un mensaje de error si las credenciales no son correctas
        this.errorMessage = 'Email o contraseña incorrectos';
      }
    });
  }
}

