import { Business } from './Business';
import { Resource } from './Resource';
import { ServiceDuration } from './ServiceDuration';
import { Schedule } from './Schedule';

export interface Service {
  id: number;
  nombre: string;
  tipoServicio: {
    id: number;
    nombre: string;
  };
  recurso?: Resource | null;
  negocio: Business;
  duracionServicio: ServiceDuration;
  costo?: number;
  dias_cancelacion?: number;
  porcentaje_reembolso?: number;
  horariosAtencionServicios: Schedule[];
  trabajadores_simultaneos?: number;
  asignacion_automatica?: boolean;
}
