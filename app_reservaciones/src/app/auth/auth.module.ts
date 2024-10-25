import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Agregar FormsModule

import { AuthRoutingModule } from './auth-routing.module';
import { MaterialModule } from '../material/material.module';  // Asegúrate de importar MaterialModule

import { AuthViewComponent } from './components/auth-view/auth-view.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';

@NgModule({
  declarations: [
    //Views
    AuthViewComponent,
    //Components
    LoginComponent,
    RegisterComponent,
    ResetPasswordComponent,
    //ConfirmMailComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    AuthRoutingModule,
    MaterialModule,
  ]
})
export class AuthModule { }
