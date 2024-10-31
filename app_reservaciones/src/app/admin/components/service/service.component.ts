import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { ServiceService } from '../../services/service.service';
import { Service } from '../../../models/Service';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';

@Component({
  selector: 'app-services',
  templateUrl: './service.component.html',
  styleUrls: ['./service.component.css'],
})
export class ServiceComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nombre', 'tipoServicio', 'negocio', 'acciones'];
  dataSource: MatTableDataSource<Service> = new MatTableDataSource();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private serviceService: ServiceService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices(): void {
    this.serviceService.getAllServices().subscribe({
      next: (services) => {
        console.log('Servicios obtenidos:', services);
        this.dataSource.data = services;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: () => {
        this.dialog.open(DialogComponent, {
          data: {
            title: 'Error',
            description: 'No se pudieron cargar los servicios. Intente nuevamente.',
            backgroundColor: 'red',
          },
        });
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

  crearServicio(): void {
    this.router.navigate(['/admin/service-create']);
  }

  editarServicio(service: Service): void {
    this.router.navigate(['/admin/service-edit', service.id]);
  }

  eliminarServicio(id: number): void {
    const confirmDialog = this.dialog.open(DialogComponent, {
      data: {
        title: 'Confirmar eliminación',
        description: '¿Estás seguro de que deseas eliminar este servicio?',
        backgroundColor: 'red',
      },
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.serviceService.deleteService(id).subscribe({
          next: () => {
            this.loadServices();
            this.dialog.open(DialogComponent, {
              data: {
                title: 'Éxito',
                description: 'Servicio eliminado con éxito.',
                backgroundColor: 'green',
              },
            });
          },
          error: () => {
            this.dialog.open(DialogComponent, {
              data: {
                title: 'Error',
                description: 'Error al eliminar el servicio.',
                backgroundColor: 'red',
              },
            });
          },
        });
      }
    });
  }
}
