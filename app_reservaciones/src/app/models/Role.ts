import { Permission } from './Permission';
import { Service } from './Service';

export interface Role {
  id: number;
  nombre: string; // Nombre del rol (ej., ADMIN, CLIENTE)
  permisos: Permission[]; // Lista de permisos asignados a este rol
  servicios?: Service[]; // Servicios asociados al rol, si los tiene
}
