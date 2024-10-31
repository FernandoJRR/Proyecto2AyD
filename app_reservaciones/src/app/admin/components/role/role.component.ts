import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { RoleService } from '../../services/role.service';
import { Role } from '../../../models/Role';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';

@Component({
  selector: 'app-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.css'],
})
export class RoleComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nombre', 'acciones'];
  dataSource: MatTableDataSource<Role> = new MatTableDataSource();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private roleService: RoleService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    console.log('Iniciando carga de roles desde la API...');
    this.roleService.getAllRoles().subscribe({
      next: (roles: Role[]) => {
        console.log('Roles obtenidos de la API:', roles);
        this.dataSource.data = roles;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (error) => {
        console.error('Error al cargar los roles:', error);
        this.openDialog('Error', 'No se pudieron cargar los roles. Intente nuevamente.', 'red');
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

  crearRol(): void {
    console.log('Redirigiendo a creación de nuevo rol');
    this.router.navigate(['/admin/role-create']);
  }

  editarRol(rol: Role): void {
    console.log('Redirigiendo a edición del rol con ID:', rol.id);
    this.router.navigate(['/admin/role-edit', rol.id]);
  }

  eliminarRol(id: number): void {
    console.log('Preparando eliminación del rol con ID:', id);
    const confirmDialog = this.dialog.open(DialogComponent, {
      data: {
        title: 'Confirmar eliminación',
        description: '¿Estás seguro de que deseas eliminar este rol?',
        backgroundColor: 'red',
      },
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.roleService.deleteRole(id).subscribe({
          next: () => {
            console.log('Rol eliminado exitosamente');
            this.loadRoles(); // Recargar la lista después de eliminar
            this.openDialog('Eliminado', 'El rol ha sido eliminado con éxito.', 'green');
          },
          error: (error) => {
            console.error('Error al eliminar el rol:', error);
            this.openDialog('Error', 'Error al eliminar el rol. Intente nuevamente.', 'red');
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
