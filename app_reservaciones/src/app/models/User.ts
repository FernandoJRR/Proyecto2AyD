// export interface User {
//     id: number;
//     nombres: string;
//     apellidos: string;
//     email: string;
//     nit: string;
//     cui: string;
//     telefono: string;
//     roles: { rol: UserRole }[];
//     twoFactorEnabled: boolean;
//     twoFactorCode?: string;
//     verificado: number;
//   }
import { Role } from './Role';

export interface User {
  id: number;
  createdAt: string;
  nombres: string;
  apellidos: string;
  email: string;
  nit: string;
  cui: string;
  telefono: string;
  verificado: boolean;
  twoFactorEnabled: boolean;
  roles: Role[]; // Los roles asociados a este usuario
}
