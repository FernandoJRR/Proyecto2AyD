import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MenubarModule } from "primeng/menubar";
import { AvatarModule } from "primeng/avatar";
import { BadgeModule } from "primeng/badge";
import { ButtonModule } from "primeng/button";
import { ButtonGroupModule } from "primeng/buttongroup";
import { InputTextModule } from "primeng/inputtext";
import { DividerModule } from "primeng/divider";
import { CheckboxModule } from "primeng/checkbox";
import { SelectButtonModule } from "primeng/selectbutton";
import { ChipModule } from "primeng/chip";
import { PanelModule } from "primeng/panel";
import { StepperModule } from "primeng/stepper";
import { FieldsetModule } from "primeng/fieldset";
import { ListboxModule } from "primeng/listbox";
import { ScrollPanelModule } from "primeng/scrollpanel";
import { InputNumber, InputNumberModule } from "primeng/inputnumber";
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { DataViewModule } from "primeng/dataview";
import { TagModule } from "primeng/tag";
import { CalendarModule as PCalendarModule } from "primeng/calendar";
import { CardModule } from "primeng/card";

import { ClientRoutingModule } from './client-routing.module';

import { ClientViewComponent } from './components/client-view/client-view.component';
import { MaterialModule } from '../material/material.module';
import { ProfileComponent } from './components/profile/profile.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NuevaReservacionComponent } from './components/nueva-reservacion/nueva-reservacion.component';
import { ReservacionExitosaComponent } from './components/reservacion-exitosa/reservacion-exitosa.component';
import { ReservacionesComponent } from './components/reservaciones/reservaciones.component';
import { ReservacionComponent } from './components/reservacion/reservacion.component';
import { CancelarReservacionComponent } from './components/cancelar-reservacion/cancelar-reservacion.component';


@NgModule({
  declarations: [
    //Views
    ClientViewComponent,
    ProfileComponent,
    NuevaReservacionComponent,
    ReservacionExitosaComponent,
    ReservacionesComponent,
    ReservacionComponent,
    CancelarReservacionComponent,
    //Components,
  ],
  imports: [
    CommonModule,
    ClientRoutingModule,
    MaterialModule,
    MenubarModule,
    AvatarModule,
    BadgeModule,
    InputTextModule,
    ButtonModule,
    ButtonGroupModule,
    CheckboxModule,
    SelectButtonModule,
    StepperModule,
    FieldsetModule,
    ListboxModule,
    ChipModule,
    PanelModule,
    ScrollPanelModule,
    InputNumberModule,
    ReactiveFormsModule,
    FormsModule,
    DividerModule,
    CalendarModule.forRoot({
        provide: DateAdapter,
        useFactory: adapterFactory,
      }),
      PCalendarModule,
      DataViewModule,
      TagModule,
      CardModule
  ]
})
export class ClientModule { }
