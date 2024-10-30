import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClientViewComponent } from './components/client-view/client-view.component';

const routes: Routes = [
  { path: '', component: ClientViewComponent, children: [
    //{ path: 'my-account', component: MyAccountComponent },
  ]}

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientRoutingModule { }
