import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AdminViewComponent } from './components/admin-view/admin-view.component';
import { UserComponent } from './components/user/user.component';
import { RoleComponent } from './components/role/role.component';
import { PermissionComponent } from './components/permission/permission.component';
import { BusinessComponent } from './components/business/business.component';
import { BusinessEditComponent } from './components/business-edit/business-edit.component';
import { BusinessCreateComponent } from './components/business-create/business-create.component';
import { ResourceComponent } from './components/resource/resource.component';
import { ResourceCreateComponent } from './components/resource-create/resource-create.component';
import { ResourceEditComponent } from './components/resource-edit/resource-edit.component';

const routes: Routes = [
  { path: '', component: AdminViewComponent, children: [
    //{ path: 'overview', component: OverviewComponent },
    //Negocios
    { path: 'business', component: BusinessComponent },
    { path: 'business-create', component: BusinessCreateComponent },
    { path: 'business-edit/:id', component: BusinessEditComponent },
    //Recursos
    { path: 'resources', component: ResourceComponent },
    { path: 'resource-create', component: ResourceCreateComponent },
    { path: 'resource-edit/:id', component: ResourceEditComponent },
    //Usuarios
    { path: 'users', component: UserComponent },
    //Permisos
    { path: 'permissions', component: PermissionComponent },
    //Roles
    { path: 'roles', component: RoleComponent },
  ]}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
