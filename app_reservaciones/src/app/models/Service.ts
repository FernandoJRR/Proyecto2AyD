import { Business } from './Business';

export interface Service {
  id: number;
  nombre: string; // Nombre del servicio
  costo: number;
  diasCancelacion: number;
  porcentajeReembolso: number;
  trabajadoresSimultaneos: number;
  negocio: Business; // Negocio al que pertenece el servicio
}
