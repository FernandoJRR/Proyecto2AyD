import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-view',
  templateUrl: './admin-view.component.html',
  styleUrls: ['./admin-view.component.css']
})
export class AdminViewComponent {
  opened = true;

  constructor(private router: Router) {}

  toggleSidebar() {
    this.opened = !this.opened;
  }

  logout() {
    // Agrega la lógica de cierre de sesión aquí
    this.router.navigate(['/auth/login']);
  }
}
