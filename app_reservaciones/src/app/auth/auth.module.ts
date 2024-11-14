import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // Agregar FormsModule
import { InputTextModule } from "primeng/inputtext";
import { FloatLabelModule } from "primeng/floatlabel";
import { PasswordModule } from "primeng/password";
import { InputOtpModule } from "primeng/inputotp";

import { AuthRoutingModule } from './auth-routing.module';
import { MaterialModule } from '../material/material.module';  // Asegúrate de importar MaterialModule

import { AuthViewComponent } from './components/auth-view/auth-view.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { DialogComponent } from '../utils/dialog/dialog.component';
import { UserVerifyComponent } from './components/user-verify/user-verify.component';
import { TwoFactorComponent } from './components/two-factor/two-factor.component';
import { PasswordRecoveryComponent } from './components/password-recovery/password-recovery.component';

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
    PasswordRecoveryComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    InputTextModule,
    FloatLabelModule,
    PasswordModule,
    AuthRoutingModule,
    MaterialModule,
    ReactiveFormsModule,
    InputOtpModule
  ]
})
export class AuthModule { }
