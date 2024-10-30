import { Role } from './Role';
import { Schedule } from './Schedule';
import { UserRole } from './UserRole';

export interface User {
  id: number;
  createdAt: string;
  nombres: string;
  apellidos: string;
  email: string;
  nit?: string;
  cui?: string;
  password: string;
  telefono?: string;
  roles: UserRole[];
  horariosAtencionUsuario?: Schedule[];
  validated?: boolean; //Asi aparece en la API/Backend
  verificado: boolean; //Asi lo usamos en el Frontend porque asi esta en la DB
  twoFactorEnabled: boolean;
  twoFactorCode: string; //PARA CUANDO ESTE HABILITADO EL TWO FACTOR AUTH
}
