import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { PermissionService } from '../../services/permission.service';
import { Permission } from '../../../models/Permission';

@Component({
  selector: 'app-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.css'],
})
export class PermissionComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nombre', 'ruta'];
  dataSource: MatTableDataSource<Permission> = new MatTableDataSource();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private permissionService: PermissionService) {}

  ngOnInit(): void {
    this.fetchPermissions();
  }

  fetchPermissions(): void {
    this.permissionService.getPermissions().subscribe({
      next: (permissions: Permission[]) => {
        console.log('Permisos obtenidos:', permissions);
        this.dataSource.data = permissions;
        console.log('Datos en dataSource:', this.dataSource.data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (error) => {
        console.error('Error al obtener los permisos:', error);
        this.permissionService.openConfirmationDialog(
          'Error al obtener permisos',
          'No se pudieron cargar los permisos. Intente nuevamente.',
          'red'
        );
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
}
