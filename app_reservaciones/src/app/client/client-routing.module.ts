import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClientViewComponent } from './components/client-view/client-view.component';
import { ProfileComponent } from './components/profile/profile.component';

const routes: Routes = [
  { path: '', component: ClientViewComponent, children: [
      { path: 'profile', component: ProfileComponent },
  ]}

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientRoutingModule { }
