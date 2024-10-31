import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // Agregar FormsModule

import { AdminRoutingModule } from './admin-routing.module';
import { MaterialModule } from '../material/material.module';

import { AdminViewComponent } from './components/admin-view/admin-view.component';
import { PermissionComponent } from './components/permission/permission.component';
import { RoleComponent } from './components/role/role.component';
import { UserComponent } from './components/user/user.component';
import { BusinessComponent } from './components/business/business.component';
import { BusinessEditComponent } from './components/business-edit/business-edit.component';
import { BusinessCreateComponent } from './components/business-create/business-create.component';
import { ResourceComponent } from './components/resource/resource.component';
import { ResourceCreateComponent } from './components/resource-create/resource-create.component';
import { ResourceEditComponent } from './components/resource-edit/resource-edit.component';
import { ServiceCreateComponent } from './components/service-create/service-create.component';
import { ServiceEditComponent } from './components/service-edit/service-edit.component';
import { ServiceComponent } from './components/service/service.component';
import { RoleCreateComponent } from './components/role-create/role-create.component';

@NgModule({
  declarations: [
    //Views
    AdminViewComponent,
    //Components
    //Permissions
    PermissionComponent,
    //Businesses
    BusinessComponent,
    BusinessCreateComponent,
    BusinessEditComponent,
    //Resources
    ResourceComponent,
    ResourceCreateComponent,
    ResourceEditComponent,
    //Services
    ServiceComponent,
    ServiceCreateComponent,
    ServiceEditComponent,

    //------------------------------
    
    //Roles
    RoleComponent,
    //RoleCreateComponent,
    //RoleEditComponent,
    //Users
    UserComponent,
    RoleCreateComponent,
    //UserCreateComponent,
    //UserEditComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AdminRoutingModule,
    MaterialModule
  ]
})
export class AdminModule { }
