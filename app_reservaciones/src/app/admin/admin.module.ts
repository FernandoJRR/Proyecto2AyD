import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Agregar FormsModule

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

@NgModule({
  declarations: [
    //Views
    AdminViewComponent,
    //Components,
    PermissionComponent,
    //ServiceTypeComponent,
    //ResourceUnit,
    //Service,
    RoleComponent,
    UserComponent,
    BusinessComponent,
    BusinessEditComponent,
    BusinessCreateComponent,
    ResourceComponent,
    ResourceCreateComponent,
    ResourceEditComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    AdminRoutingModule,
    MaterialModule
  ]
})
export class AdminModule { }
