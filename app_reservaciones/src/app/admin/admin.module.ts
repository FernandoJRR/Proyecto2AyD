import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Agregar FormsModule

import { AdminRoutingModule } from './admin-routing.module';
import { MaterialModule } from '../material/material.module';

import { AdminViewComponent } from './components/admin-view/admin-view.component';
import { PermissionComponent } from './components/permission/permission.component';
import { RoleComponent } from './components/role/role.component';
import { UserComponent } from './components/user/user.component';

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

  ],
  imports: [
    CommonModule,
    FormsModule,
    AdminRoutingModule,
    MaterialModule,
  ]
})
export class AdminModule { }
