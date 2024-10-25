export interface Service {
  id: number;
  createdAt: string;
  nombre: string;

  //SrviceType
  //Resource
  //Business
  //ServiceDuration


  tipoServicio: {
    id: number;
    createdAt: string;
    nombre: string;
  };
  recurso: {
    id: number;
    createdAt: string;
    nombre: string;
  };
  negocio: {
    id: number;
    createdAt: string;
    nombre: string;
  };
  duracionServicio: {
    id: number;
    createdAt: string;
    servicio: string;
    minutos: number;
    horas: number;
  };
  trabajadores_simultaneos: number;
}


