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

import { ClientRoutingModule } from './client-routing.module';

import { ClientViewComponent } from './components/client-view/client-view.component';
import { MaterialModule } from '../material/material.module';
import { ProfileComponent } from './components/profile/profile.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NuevaReservacionComponent } from './components/nueva-reservacion/nueva-reservacion.component';


@NgModule({
  declarations: [
    //Views
    ClientViewComponent,
    ProfileComponent,
    NuevaReservacionComponent,
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
    ReactiveFormsModule,
    FormsModule,
    DividerModule
  ]
})
export class ClientModule { }
