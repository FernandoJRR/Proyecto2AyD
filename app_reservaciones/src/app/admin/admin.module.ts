import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Agregar FormsModule

import { AdminRoutingModule } from './admin-routing.module';
import { MaterialModule } from '../material/material.module';

import { AdminViewComponent } from './components/admin-view/admin-view.component';
import { PermissionComponent } from './components/permission/permission.component';
import { RoleComponent } from './components/role/role.component';
import { UserComponent } from './components/user/user.component';
import { ResourceComponent } from './components/resource/resource.component';
import { BusinessComponent } from './components/business/business.component';
import { BusinessCreateComponent } from './components/business-create/business-create.component';
import { ServiceComponent } from './components/service/service.component';


@NgModule({
  declarations: [
    //Views
    AdminViewComponent,
    //Components,
    PermissionComponent,
    BusinessComponent,
    BusinessCreateComponent,
    //ServiceTypeComponent,
    ResourceComponent,
    //ResourceUnit,
    //Service,
    RoleComponent,
    UserComponent,
    ServiceComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    AdminRoutingModule,
    MaterialModule,
  ]
})
export class AdminModule { }
