import { importProvidersFrom, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // Agregar FormsModule
import { MenubarModule } from "primeng/menubar";
import { ButtonModule } from "primeng/button";
import { BadgeModule } from "primeng/badge";
import { AvatarModule } from "primeng/avatar";
import { CardModule } from "primeng/card";
import { FieldsetModule } from "primeng/fieldset";
import { ListboxModule } from "primeng/listbox";
import { TableModule } from "primeng/table";
import { DataViewModule } from "primeng/dataview";
import { TagModule } from "primeng/tag";
import { ChipModule } from "primeng/chip";
import { FileUploadModule } from "primeng/fileupload";
import { DropdownModule } from "primeng/dropdown";
import { InputNumberModule } from "primeng/inputnumber";

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
import { HelperCreateComponent } from './components/helper-create/helper-create.component';
import { ProfileComponent } from './components/profile/profile.component';
import { ButtonGroupModule } from "primeng/buttongroup";
import { DividerModule } from "primeng/divider";
import { InputTextModule } from "primeng/inputtext";
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ResourceUnitComponent } from './components/resource-unit/resource-unit.component';
import { ResourceUnitsCreateComponent } from './components/resource-units-create/resource-units-create.component';
import { UserEditComponent } from './components/user-edit/user-edit.component';
import { ReservacionesComponent } from './components/reservaciones/reservaciones.component';
import { ReservacionComponent } from './components/reservacion/reservacion.component';
import { ConfigComponent } from './components/config/config.component';

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
    HelperCreateComponent,
    ProfileComponent,
    DashboardComponent,
    ResourceUnitComponent,
    ResourceUnitsCreateComponent,
    UserEditComponent,
    ReservacionesComponent,
    ReservacionComponent,
    ConfigComponent,
    //UserCreateComponent,
    //UserEditComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AdminRoutingModule,
    MaterialModule,
    MenubarModule,
    ButtonModule,
    BadgeModule,
    ButtonGroupModule,
    DividerModule,
    InputTextModule,
    CardModule,
    ListboxModule,
    FieldsetModule,
    TableModule,
    DataViewModule,
    TagModule,
    ChipModule,
    FileUploadModule,
    DropdownModule,
    InputNumberModule,
    AvatarModule
  ]
})
export class AdminModule { }
