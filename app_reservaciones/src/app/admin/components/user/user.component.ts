import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';

interface User {
  id: number;
  nombres: string;
  apellidos: string;
  email: string;
  nit: string | null;
  cui: string | null;
  telefono: string | null;
}

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
})
export class UserComponent implements OnInit {
  displayedColumns: string[] = [
    'id',
    'nombres',
    'apellidos',
    'email',
    'nit',
    'cui',
    'telefono',
    'acciones',
  ];
  dataSource: MatTableDataSource<User> = new MatTableDataSource();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private userService: UserService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (userResponses) => {
        const users = userResponses.data.map((user: any) => ({
          id: user.id,
          nombres: user.nombres,
          apellidos: user.apellidos,
          email: user.email,
          nit: user.nit,
          cui: user.cui,
          telefono: user.telefono,
        }));
        this.dataSource.data = users;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (error) => {
        console.error('Error al cargar los usuarios:', error);
        this.openDialog(
          'Error',
          'No se pudieron cargar los usuarios. Intente nuevamente.',
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

  crearAdministrador(): void {
    this.router.navigate(['/admin/admin-create']);
  }

  crearAyudante(): void {
    this.router.navigate(['/admin/helper-create']);
  }

  crearUsuario(): void {
    this.router.navigate(['/admin/user-create']);
  }

  editarUsuario(usuario: User): void {
    console.log('Redirigiendo a edición del usuario con ID:', usuario.id);
  }

  eliminarUsuario(id: number): void {
    const confirmDialog = this.dialog.open(DialogComponent, {
      data: {
        title: 'Confirmar eliminación',
        description: '¿Estás seguro de que deseas eliminar este usuario?',
        backgroundColor: 'red',
      },
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.userService.deleteUser(id).subscribe({
          next: () => {
            console.log('Usuario eliminado exitosamente');
            this.loadUsers();
            this.openDialog(
              'Eliminado',
              'El usuario ha sido eliminado con éxito.',
              'green'
            );
          },
          error: (error) => {
            console.error('Error al eliminar el usuario:', error);
            this.openDialog(
              'Error',
              'Error al eliminar el usuario. Intente nuevamente.',
              'red'
            );
          },
        });
      }
    });
  }

  openDialog(
    title: string,
    description: string,
    backgroundColor: 'red' | 'green' | 'gray'
  ): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }
}
