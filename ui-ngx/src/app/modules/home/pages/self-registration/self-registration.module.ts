import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';
import { SelfRegistrationRoutingModule } from './self-registration-routing.module';
import { UserGroupRolesComponent } from '../../components/user-group-roles/user-group-roles.component';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SharedModule,
    SelfRegistrationRoutingModule
  ]
})
export class SelfRegistrationModule { }
