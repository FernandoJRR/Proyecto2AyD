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
  selector: 'app-service',
  templateUrl: './service.component.html',
  styleUrls: ['./service.component.css']
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
    console.log('Iniciando carga de servicios desde la API...');
    this.serviceService.getAllServices().subscribe({
      next: (services: Service[]) => {
        console.log('Servicios obtenidos de la API:', services);
        this.dataSource.data = services;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (error) => {
        console.error('Error al cargar los servicios:', error);
        this.openDialog('Error', 'No se pudieron cargar los servicios. Intente nuevamente.', 'red');
      }
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
    console.log('Redirigiendo a creación de nuevo servicio');
    this.router.navigate(['/admin/service-create']);
  }

  editarServicio(servicio: Service): void {
    console.log('Redirigiendo a edición del servicio con ID:', servicio.id);
    this.router.navigate(['/admin/service-edit', servicio.id]);
  }

  eliminarServicio(id: number): void {
    console.log('Preparando eliminación del servicio con ID:', id);
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
            console.log('Servicio eliminado exitosamente');
            this.loadServices(); // Recargar la lista después de eliminar
            this.openDialog('Eliminado', 'El servicio ha sido eliminado con éxito.', 'green');
          },
          error: (error) => {
            console.error('Error al eliminar el servicio:', error);
            this.openDialog('Error', 'Error al eliminar el servicio. Intente nuevamente.', 'red');
          },
        });
      }
    });
  }

  openDialog(title: string, description: string, backgroundColor: 'red' | 'green' | 'gray'): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }
}
