import { Component, OnInit } from '@angular/core';
import { DialogComponent } from '../../../utils/dialog/dialog.component';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { User } from '../../../models/User';
import { RoleService } from '../../services/role.service';
import { Role } from '../../../models/Role';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrl: './user-edit.component.css'
})
export class UserEditComponent implements OnInit {
  user: any = {
    id: 0,
    nombres: '',
    apellidos: '',
    horariosAtencionUsuario: [],
    roles: []
  };

  diasSemana = ['Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado', 'Domingo'];

  constructor(
    private userService: UserService,
    private rolService: RoleService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog
  ) {}

  roles: Array<Role> = []

  ngOnInit(): void {
    const userId = this.route.snapshot.paramMap.get('id');
    if (userId) {
      console.log('ID del negocio a editar:', userId);
      this.loadUser(Number(userId));
      this.loadRoles()
    }
  }

  loadRoles(): void {
    this.rolService.getAllRoles().subscribe({
        next: (response) => {
            this.roles = response
        },
        error: (error) => {
            console.log("ERROR al obtener los roles")
            console.log(error)
        }
    })
  }

  loadUser(id: number): void {
    this.userService.getUser(id).subscribe({
      next: (data: User) => {
        console.log('Datos del negocio obtenidos de la API:', data);
        this.user = data;
        console.log('Objeto Business asignado:', this.user);
      },
      error: () => {
        this.openDialog('Error', 'Error al cargar los datos del usuario.', 'red');
      }
    });
  }

  onSubmit(): void {
    if (!this.user.nombres) {
      this.openDialog('Campo vacío', 'El nombre del negocio no puede estar vacío.', 'red');
      return;
    }

    console.log("PETICION")
    console.log(this.user)
    this.userService.updateUserFull(this.user).subscribe({
      next: () => {
        this.openDialog('Actualización exitosa', 'Usuario actualizado con éxito.', 'green');
        this.router.navigate(['/admin/users']);
      },
      error: () => {
        this.openDialog('Error', 'Error al actualizar el negocio.', 'red');
      }
    });
  }

  addHorario(): void {
    this.user.horariosAtencionUsuario.push({
      horaInicio: '',
      horaFinal: '',
      diaAtencion: { nombre: '' },
    });
  }

  removeHorario(index: number): void {
    this.user.horariosAtencionUsuario.splice(index, 1);
  }

  addRol(): void {
    this.user.roles.push({
      rol: {nombre: ''},
    });
  }

  removeRol(index: number): void {
    this.user.roles.splice(index, 1);
  }

  onCancel(): void {
    this.router.navigate(['/admin/users']);
  }

  openDialog(title: string, description: string, backgroundColor: 'red' | 'green' | 'gray'): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }
}
