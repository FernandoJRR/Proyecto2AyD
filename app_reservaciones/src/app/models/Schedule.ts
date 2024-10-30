export interface Schedule {
    id: number;
    createdAt: string;
    horaInicio: Time;
    horaFinal: Time;
  }
  
  export interface Time {
    hour: number;
    minute: number;
    second: number;
    nano: number;
  }
  