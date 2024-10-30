import { Business } from './Business';
//import { Resource } from './Resource';
//import { ServiceType } from './ServiceType';
import { Schedule } from './Schedule';

export interface Service {
  id: number;
  createdAt: string;
  nombre: string;
  costo: number;
  dias_cancelacion: number;
  porcentaje_reembolso: number;
  trabajadores_simultaneos: number;
  negocio: Business;
  //recurso: Resource;
  //tipoServicio: ServiceType;
  horariosAtencionServicios: Schedule[];
}
