import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ResourceService } from '../../services/resource.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';
import { Resource } from '../../../models/Resource';

@Component({
  selector: 'app-resource-create',
  templateUrl: './resource-create.component.html',
  styleUrls: ['./resource-create.component.css']
})
export class ResourceCreateComponent {
  resource: Resource = {
    id: 0,
    nombre: ''
  };

  constructor(
    private resourceService: ResourceService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  onSubmit(): void {
    if (!this.resource.nombre) return;

    this.resourceService.createResource(this.resource).subscribe(
      () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Recurso Creado',
            description: 'El recurso se ha creado exitosamente.',
          },
        });
        this.router.navigate(['/admin/resource']);
      },
      () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Error',
            description: 'No se pudo crear el recurso.',
          },
        });
      }
    );
  }

  onCancel(): void {
    this.router.navigate(['/admin/resource']);
  }
}
