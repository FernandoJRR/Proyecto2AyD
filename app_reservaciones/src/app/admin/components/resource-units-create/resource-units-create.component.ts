import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Resource } from '../../../models/Resource';
import { ResourceService } from '../../services/resource.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';
import { ResourceUnit } from '../../../models/ResourceUnit';

@Component({
  selector: 'app-resource-units-create',
  templateUrl: './resource-units-create.component.html',
  styleUrl: './resource-units-create.component.css',
})
export class ResourceUnitsCreateComponent implements OnInit {
  resourceUnit: Resource = {
    id: 0,
    nombre: '',
  };
  columns = [
    { field: 'id', header: 'ID' },
    { field: 'nombre', header: 'Nombre' },
  ];
  constructor(
    private route: ActivatedRoute,
    private resourceService: ResourceService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  recurso: Resource | null = null;
  unidadesRecurso: Array<ResourceUnit> = [];

  ngOnInit() {
    const resourceId = Number.parseInt(
      this.route.snapshot.paramMap.get('id') || '0'
    );

    this.resourceService.getResourceById(resourceId).subscribe({
      next: (response) => {
        this.recurso = response;

        this.resourceService
          .getResourceUnitByResource(this.recurso.id)
          .subscribe({
            next: (response) => {
              console.log('unidades recurso obtenidas:', response);
              this.unidadesRecurso = response;
            },
            error: (error) => {
              console.log('ERROR al obtener las unidades de recurso');
              console.log(error);
            },
          });
      },
      error: (error) => {
        console.log('ERROR al obtener el recurso');
        console.log(error);
      },
    });
  }

  onSubmit(): void {
    // Validación del campo 'nombre'
    if (!this.resourceUnit.nombre.trim()) {
      // Muestra el diálogo de advertencia si el campo está vacío
      this.dialog.open(DialogComponent, {
        data: {
          title: 'Campo Requerido',
          description:
            'El campo "Nombre de la Unidad" no puede estar vacío. Por favor ingresa un nombre válido.',
          backgroundColor: 'red',
        },
      });
      return;
    }

    // Continúa con la creación si el campo es válido
    this.resourceService
      .createResourceUnit({
        nombre: this.resourceUnit.nombre,
        recurso: { id: this.recurso?.id },
      })
      .subscribe({
        next: () => {
          this.dialog.open(DialogComponent, {
            data: {
              title: 'Unidad Creada',
              description: 'La unidad se ha creado con éxito.',
              backgroundColor: 'green',
            },
          });
          this.router.navigate(['/admin/resource-units']);
        },
        error: () => {
          this.dialog.open(DialogComponent, {
            data: {
              title: 'Error',
              description:
                'Hubo un problema al crear la unidad. Inténtalo de nuevo.',
              backgroundColor: 'red',
            },
          });
        },
      });
  }

  onCancel(): void {
    this.router.navigate(['/admin/resource-units']);
  }
}
