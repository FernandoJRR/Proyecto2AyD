import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BusinessService } from '../../services/business.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';
import { Business } from '../../../models/Business';

@Component({
  selector: 'app-business-create',
  templateUrl: './business-create.component.html',
  styleUrls: ['./business-create.component.css']
})
export class BusinessCreateComponent {
  business: Business = {
    id: 0,
    nombre: '',
  };

  constructor(
    private businessService: BusinessService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  onSubmit(): void {
    // Validación del campo 'nombre'
    if (!this.business.nombre.trim()) {
      // Muestra el diálogo de advertencia si el campo está vacío
      this.dialog.open(DialogComponent, {
        data: {
          title: 'Campo Requerido',
          description: 'El campo "Nombre del Negocio" no puede estar vacío. Por favor ingresa un nombre válido.',
          backgroundColor: 'red'
        }
      });
      return;
    }

    // Continúa con la creación si el campo es válido
    this.businessService.createBusiness(this.business).subscribe({
      next: () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Negocio Creado',
            description: 'El negocio se ha creado con éxito.',
            backgroundColor: 'green'
          }
        });
        this.router.navigate(['/admin/business']);
      },
      error: () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Error',
            description: 'Hubo un problema al crear el negocio. Inténtalo de nuevo.',
            backgroundColor: 'red'
          }
        });
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/business']);
  }
}
