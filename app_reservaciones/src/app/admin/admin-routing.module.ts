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
import { ServiceComponent } from './components/service/service.component';
import { ServiceCreateComponent } from './components/service-create/service-create.component';
import { ServiceEditComponent } from './components/service-edit/service-edit.component';
import { RoleCreateComponent } from './components/role-create/role-create.component';
import { HelperCreateComponent } from './components/helper-create/helper-create.component';

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
    
    //PEND. "UNIDAD DE RECURSO"

    //Permisos
    { path: 'permissions', component: PermissionComponent },
    //Services
    { path: 'services', component: ServiceComponent },
    { path: 'service-create', component: ServiceCreateComponent },
    { path: 'service-edit', component: ServiceEditComponent },
    //Roles
    { path: 'roles', component: RoleComponent },
    { path: 'role-create', component: RoleCreateComponent },
    //Usuarios
    { path: 'users', component: UserComponent },
    //{ path: 'admin-create', component: AdminCreateComponent },
    { path: 'helper-create', component: HelperCreateComponent },
    //{ path: 'user-create', component: UserCreateComponent },
    
  ]}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
