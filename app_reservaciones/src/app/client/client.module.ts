import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientRoutingModule } from './client-routing.module';

import { ClientViewComponent } from './components/client-view/client-view.component';
import { MaterialModule } from '../material/material.module';


@NgModule({
  declarations: [
    //Views
    ClientViewComponent,
    //Components,
  ],
  imports: [
    CommonModule,
    ClientRoutingModule,
    MaterialModule
  ]
})
export class ClientModule { }
