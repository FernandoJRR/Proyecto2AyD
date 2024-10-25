import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthViewComponent } from './components/auth-view/auth-view.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';

const routes: Routes = [
  { path: '', component: AuthViewComponent, children: [
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
  ]},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
