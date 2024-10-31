import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RoleService } from '../../services/role.service';
import { RoleCreationRequest } from '../../../models/RoleCreationRequest';
import { Service } from '../../../models/Service';
import { Permission } from '../../../models/Permission';
import { ServiceService } from '../../services/service.service';
import { PermissionService } from '../../services/permission.service';

@Component({
  selector: 'app-role-create',
  templateUrl: './role-create.component.html',
  styleUrls: ['./role-create.component.css'],
})
export class RoleCreateComponent implements OnInit {
  roleForm: FormGroup;
  servicios: Service[] = [];
  permisos: Permission[] = [];
  selectedPermisos: Permission[] = [];
  selectedServicios: Service[] = [];

  constructor(
    private fb: FormBuilder,
    private roleService: RoleService,
    private permissionService: PermissionService,
    private serviceService: ServiceService
  ) {
    this.roleForm = this.fb.group({
      nombre: ['', Validators.required],
      permisos: [[]], // Inicializa como un array vacío
      servicios: [[]], // Inicializa como un array vacío
    });
  }

  ngOnInit(): void {
    this.loadServices();
    this.loadPermissions();
  }

  loadServices(): void {
    this.serviceService.getAllServices().subscribe((data) => {
      this.servicios = data;
      console.log('Servicios cargados:', this.servicios);
    });
  }

  loadPermissions(): void {
    this.permissionService.getAllPermissions().subscribe((data) => {
      this.permisos = data;
      console.log('Permisos cargados:', this.permisos);
    });
  }

  onSubmit(): void {
    if (this.roleForm.valid) {
      const roleData: RoleCreationRequest = {
        rol: { nombre: this.roleForm.get('nombre')?.value },
        permisos: this.roleForm.get('permisos')?.value.map((id: number) => ({ id })), // Especificar el tipo aquí
        servicios: this.roleForm.get('servicios')?.value.map((id: number) => ({ id })), // Especificar el tipo aquí
      };
  
      console.log('Objeto Role construido:', roleData);
  
      this.roleService.createRole(roleData).subscribe({
        next: (response) => {
          console.log('Respuesta de la API al crear rol:', response);
          alert('Rol creado exitosamente');
        },
        error: (error) => {
          console.error('Error al crear el rol:', error);
        },
      });
    } else {
      console.log('Formulario inválido');
    }
  }
  
}
