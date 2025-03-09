import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@app/shared/public-api';
import { SelfRegistrationComponent } from '../admin/self-registration.component';

export const selfRegistrationRoutes: Routes = [
  {
    path: 'self-registration',
    component: SelfRegistrationComponent,
    data: {
      auth: [Authority.TENANT_ADMIN],
      title: 'registration.self-registration',
      breadcrumb: {
        label: 'registration.self-registration',
        icon: 'group_add'
      },
    }
  }
];

const routes: Routes = [
  {
    path: 'self-registration',
    redirectTo: '/security-settings/self-registration',
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: []
})
export class SelfRegistrationRoutingModule { }
