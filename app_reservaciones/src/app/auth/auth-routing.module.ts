import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthViewComponent } from './components/auth-view/auth-view.component';
import { RegisterComponent } from './components/register/register.component';
import { UserVerifyComponent } from './components/user-verify/user-verify.component';
import { LoginComponent } from './components/login/login.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { TwoFactorComponent } from './components/two-factor/two-factor.component';

const routes: Routes = [
  {
    path: '',
    component: AuthViewComponent,
    children: [
      { path: '', redirectTo: '/login', pathMatch: 'full' },
      { path: 'register', component: RegisterComponent },
      { path: 'user-verify', component: UserVerifyComponent },
      //{ path: 'user_verify/form', component: UserVerifyComponent },
      { path: 'login', component: LoginComponent },
      { path: 'two-factor', component: TwoFactorComponent},
      { path: 'reset-password', component: ResetPasswordComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuthRoutingModule {}
