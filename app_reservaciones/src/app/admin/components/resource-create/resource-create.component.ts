import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ResourceService } from '../../services/resource.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';
import { Resource } from '../../../models/Resource';

@Component({
  selector: 'app-resource-create',
  templateUrl: './resource-create.component.html',
  styleUrls: ['./resource-create.component.css'],
})
export class ResourceCreateComponent {
  resource: Resource = { id: 0, nombre: '' };

  constructor(
    private resourceService: ResourceService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  onSubmit(): void {
    if (!this.resource.nombre) {
      this.dialog.open(DialogComponent, {
        data: {
          title: 'Campo Requerido',
          description: 'Debe ingresar un nombre para el recurso.',
          backgroundColor: 'red',
        },
      });
      return;
    }

    this.resourceService.createResource(this.resource).subscribe({
      next: () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Éxito',
            description: 'Recurso creado con éxito.',
            backgroundColor: 'green',
          },
        });
        this.router.navigate(['/admin/resources']);
      },
      error: () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Error',
            description: 'Hubo un problema al crear el recurso.',
            backgroundColor: 'red',
          },
        });
      },
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/resources']);
  }
}
