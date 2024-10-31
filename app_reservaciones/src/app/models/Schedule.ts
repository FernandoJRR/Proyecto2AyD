import { Day } from './Day';

export interface Schedule {
  id: number;
  horaInicio: string;
  horaFinal: string;
  diaAtencion: Day;
}
