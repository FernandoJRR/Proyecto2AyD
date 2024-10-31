import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Agregar FormsModule

import { AuthRoutingModule } from './auth-routing.module';
import { MaterialModule } from '../material/material.module';  // Asegúrate de importar MaterialModule

import { AuthViewComponent } from './components/auth-view/auth-view.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { DialogComponent } from '../utils/dialog/dialog.component';
import { UserVerifyComponent } from './components/user-verify/user-verify.component';
import { TwoFactorComponent } from './components/two-factor/two-factor.component';

@NgModule({
  declarations: [
    //Views
    AuthViewComponent,
    DialogComponent, //Importacion para usar el Dialogo universal
    //Components
    LoginComponent,
    RegisterComponent,
    //ValidateAccountComponent,
    ResetPasswordComponent,
    UserVerifyComponent,
    TwoFactorComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    AuthRoutingModule,
    MaterialModule,
  ]
})
export class AuthModule { }
