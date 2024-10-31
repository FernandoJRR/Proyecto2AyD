import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ResourceService } from '../../services/resource.service';
import { MatDialog } from '@angular/material/dialog';
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
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const resourceId = this.route.snapshot.paramMap.get('id');
    if (resourceId) {
      console.log('ID del recurso a editar:', resourceId);
      this.loadResource(Number(resourceId));
    }
  }

  loadResource(id: number): void {
    this.resourceService.getResourceById(id).subscribe({
      next: (data: Resource) => {
        console.log('Datos del recurso obtenidos de la API:', data);
        this.resource = data; // Asigna el recurso, actualizando `nombre` en el input
        console.log('Objeto Resource asignado:', this.resource);
      },
      error: () => {
        this.openDialog('Error', 'Error al cargar los datos del recurso.', 'red');
      }
    });
  }

  onSubmit(): void {
    if (!this.resource.nombre) {
      this.openDialog('Campo vacío', 'El nombre del recurso no puede estar vacío.', 'red');
      return;
    }

    this.resourceService.updateResource(this.resource).subscribe({
      next: () => {
        this.openDialog('Actualización exitosa', 'Recurso actualizado con éxito.', 'green');
        this.router.navigate(['/admin/resource']);
      },
      error: () => {
        this.openDialog('Error', 'Error al actualizar el recurso.', 'red');
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/resources']);
  }

  openDialog(title: string, description: string, backgroundColor: 'red' | 'green' | 'gray'): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }
}
