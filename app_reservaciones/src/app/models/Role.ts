import { Permission } from "./Permission";
import { Service } from "./Service";

export interface Role {
    id: number;
    createdAt: string;
    nombre: string;
    servicios: Service[];
    permisos: Permission[];
  }