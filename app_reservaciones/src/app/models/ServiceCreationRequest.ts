// ServiceCreationRequest.ts
export interface ServiceCreationRequest {
  servicio: {
    nombre: string;
    tipoServicio: { id: number };
    negocio: { id: number };
    recurso?: { id: number } | null;
    asignacion_automatica: boolean;
    costo: number;
    dias_cancelacion: number;
    porcentaje_reembolso: number;
    trabajadores_simultaneos: number;
  };
  duracionServicio: {
    horas: number;
    minutos: number;
  };
  horariosAtencionServicio: {
    horaInicio: string;
    horaFinal: string;
    diaAtencion: { id: number };
  }[];
}
