import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';

import { Permission } from '../../../models/Permission';

@Component({
  selector: 'app-permissions',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.css']
})
export class PermissionComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nombre', 'descripcion', 'acciones'];
  dataSource: MatTableDataSource<Permission>;

  permissions: Permission[] = [
    { id: 1, nombre: 'Crear usuario', descripcion: 'Permite crear nuevos usuarios.' },
    { id: 2, nombre: 'Editar usuario', descripcion: 'Permite editar usuarios existentes.' },
    { id: 3, nombre: 'Ver reportes', descripcion: 'Permite ver los reportes del sistema.' },
  ];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor() {
    this.dataSource = new MatTableDataSource(this.permissions);
  }

  ngOnInit(): void {}

  ngAfterViewInit() {
    // Asigna la paginación y el sort a la tabla
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  editarPermiso(permiso: Permission) {
    // Lógica para editar un permiso
  }

  eliminarPermiso(id: number) {
    // Lógica para eliminar un permiso
    const index = this.dataSource.data.findIndex(p => p.id === id);
    if (index !== -1) {
      this.dataSource.data.splice(index, 1);
      this.dataSource._updateChangeSubscription(); // Para actualizar la tabla
    }
  }
}
