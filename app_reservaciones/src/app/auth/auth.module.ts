import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Agregar FormsModule

import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';

import { AuthViewComponent } from './components/auth-view/auth-view.component';

import { AuthRoutingModule } from './auth-routing.module';


@NgModule({
  declarations: [
    //Components
    LoginComponent,
    RegisterComponent,
    ResetPasswordComponent,
    //Views
    AuthViewComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    AuthRoutingModule
  ]
})
export class AuthModule { }
