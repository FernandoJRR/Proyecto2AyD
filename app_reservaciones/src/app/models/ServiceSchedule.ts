// ServiceSchedule.ts
import { Day } from './Day';

export interface ServiceSchedule {
    horaInicio: string;
    horaFinal: string;
    diaAtencion: Day;
}
