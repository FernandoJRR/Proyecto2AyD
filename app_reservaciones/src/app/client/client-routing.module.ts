import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClientViewComponent } from './components/client-view/client-view.component';
import { ProfileComponent } from './components/profile/profile.component';
import { NuevaReservacionComponent } from './components/nueva-reservacion/nueva-reservacion.component';
import { ReservacionExitosaComponent } from './components/reservacion-exitosa/reservacion-exitosa.component';
import { ReservacionesComponent } from './components/reservaciones/reservaciones.component';
import { ReservacionComponent } from './components/reservacion/reservacion.component';
import { CancelarReservacionComponent } from './components/cancelar-reservacion/cancelar-reservacion.component';

const routes: Routes = [
  { path: '', component: ClientViewComponent, children: [
      { path: 'profile', component: ProfileComponent },
      { path: 'nueva-reservacion', component: NuevaReservacionComponent },
      { path: 'reservacion-exitosa/:id', component: ReservacionExitosaComponent },
      { path: 'reservaciones', component: ReservacionesComponent },
      { path: 'reservacion/:id', component: ReservacionComponent },
      { path: 'cancelar-reservacion/:id', component: CancelarReservacionComponent },
  ]}

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientRoutingModule { }
