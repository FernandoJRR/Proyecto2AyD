import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { ResourceService } from '../../services/resource.service';
import { Resource } from '../../../models/Resource';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';

@Component({
  selector: 'app-resource',
  templateUrl: './resource.component.html',
  styleUrls: ['./resource.component.css'],
})
export class ResourceComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nombre', 'acciones'];
  dataSource: MatTableDataSource<Resource> = new MatTableDataSource();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private resourceService: ResourceService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadResources();
  }

  loadResources(): void {
    this.resourceService.getAllResources().subscribe({
      next: (resources) => {
        console.log('Recursos obtenidos:', resources);
        this.dataSource.data = resources;
      },
      error: () => {
        this.openErrorDialog('Error al cargar los recursos', 'No se pudieron cargar los recursos. Intente nuevamente.');
      },
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  createResource(): void {
    this.router.navigate(['/admin/resource-create']);
  }

  editResource(resource: Resource): void {
    this.router.navigate(['/admin/resource-edit', resource.id]);
  }

  deleteResource(id: number): void {
    const confirmDialog = this.dialog.open(DialogComponent, {
      data: {
        title: 'Confirmar eliminación',
        description: '¿Estás seguro de que deseas eliminar este recurso?',
        backgroundColor: 'red',
      },
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.resourceService.deleteResource(id).subscribe({
          next: () => {
            this.loadResources();
            this.openSuccessDialog('Recurso eliminado con éxito', 'El recurso ha sido eliminado correctamente.');
          },
          error: () => {
            this.openErrorDialog('Error al eliminar el recurso', 'No se pudo eliminar el recurso. Intente nuevamente.');
          },
        });
      }
    });
  }

  openErrorDialog(title: string, description: string): void {
    this.dialog.open(DialogComponent, { data: { title, description, backgroundColor: 'red' } });
  }

  openSuccessDialog(title: string, description: string): void {
    this.dialog.open(DialogComponent, { data: { title, description, backgroundColor: 'green' } });
  }
}
