import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ResourceService } from '../../services/resource.service';
import { Router } from '@angular/router';
import { Resource } from '../../../models/Resource';
import { AuthService } from '../../../auth/services/auth.service';
import { ResourceUnit } from '../../../models/ResourceUnit';

@Component({
  selector: 'app-resource-unit',
  templateUrl: './resource-unit.component.html',
  styleUrl: './resource-unit.component.css',
})
export class ResourceUnitComponent implements OnInit {
  constructor(
    private resourceService: ResourceService,
    private authService: AuthService,
    private router: Router
  ) {}
  columns = [
    { field: 'id', header: 'ID' },
    { field: 'nombre', header: 'Nombre' },
  ];

  recursos: Array<Resource> = [];
  recursoActual: Resource | null = null;
  unidadesRecurso: Array<ResourceUnit> = [];

  ngOnInit(): void {
    this.loadResources();
  }

  loadResources(): void {
    this.resourceService.getAllResources().subscribe({
      next: (resources) => {
        console.log('Recursos obtenidos:', resources);
        this.recursos = resources;
      },
      error: (error) => {
        this.recursos = [];
        console.log('ERROR al obtener recursos');
        console.log(error);
        //this.openErrorDialog('Error al cargar los recursos', 'No se pudieron cargar los recursos. Intente nuevamente.');
      },
    });
  }

  fetchUnitsResource() {
    console.log(this.recursoActual);
    if (!this.recursoActual) {
      this.unidadesRecurso = [];
      return;
    }

    this.resourceService
      .getResourceUnitByResource(this.recursoActual.id)
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
  }

  crearUnidadRecurso() {
    if (!this.recursoActual) {
      this.authService.openConfirmationDialog(
        'Error',
        'Debes seleccionar un recurso para poder crearle una unidad de recurso',
        'red'
      );
      return;
    }
    this.router.navigate([
      `/admin/resource-units-create/${this.recursoActual.id}`,
    ]);
  }
}
