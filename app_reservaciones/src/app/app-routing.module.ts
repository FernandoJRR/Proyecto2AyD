import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [

  //{ path: '', redirectTo: 'auth/login', pathMatch: 'full' }, // Correcta redirección
  //{ path: '', redirectTo: 'admin', pathMatch: 'full' }, // Correcta redirección
  { path: '', redirectTo: 'permissions', pathMatch: 'full' }, // Correcta redirección
  { path: 'auth', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
  { path: 'admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule) },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
