import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AdminViewComponent } from './components/admin-view/admin-view.component';
import { UserComponent } from './components/user/user.component';
import { RoleComponent } from './components/role/role.component';
import { PermissionComponent } from './components/permission/permission.component';
import { BusinessComponent } from './components/business/business.component';

const routes: Routes = [
  { path: '', component: AdminViewComponent, children: [
    //{ path: 'overview', component: OverviewComponent },
    { path: 'business', component: BusinessComponent },
    
    { path: 'users', component: UserComponent },
    { path: 'permissions', component: PermissionComponent },
    { path: 'roles', component: RoleComponent },
  ]}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
