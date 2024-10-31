import { Permission } from './Permission';
import { Service } from './Service';

export interface Role {
  id: number;
  nombre: string;
  permisos: Permission[];
  servicios: Service[];
}
