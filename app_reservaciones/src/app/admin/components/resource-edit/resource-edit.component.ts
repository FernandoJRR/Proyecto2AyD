import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ResourceService } from '../../services/resource.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DialogComponent } from '../../../utils/dialog/dialog.component';
import { Resource } from '../../../models/Resource';

@Component({
  selector: 'app-resource-edit',
  templateUrl: './resource-edit.component.html',
  styleUrls: ['./resource-edit.component.css']
})
export class ResourceEditComponent implements OnInit {
  resource: Resource = {
    id: 0,
    nombre: ''
  };

  constructor(
    private resourceService: ResourceService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const resourceId = this.route.snapshot.paramMap.get('id');
    if (resourceId) {
      this.loadResource(Number(resourceId));
    }
  }

  loadResource(id: number): void {
    this.resourceService.getResourceById(id).subscribe(
      (data: Resource) => {
        this.resource = data;
      },
      () => {
        this.snackBar.open('Error al cargar el recurso', 'Cerrar', {
          duration: 3000,
        });
      }
    );
  }

  onSubmit(): void {
    if (!this.resource.nombre) return;

    this.resourceService.updateResource(this.resource).subscribe(
      () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Recurso Actualizado',
            description: 'El recurso se ha actualizado exitosamente.',
          },
        });
        this.router.navigate(['/admin/resource']);
      },
      () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Error',
            description: 'No se pudo actualizar el recurso.',
          },
        });
      }
    );
  }

  onCancel(): void {
    this.router.navigate(['/admin/resource']);
  }
}
