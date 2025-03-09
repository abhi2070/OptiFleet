

import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { NgModule } from '@angular/core';
import { otaUpdatesRoutes } from '@home/pages/ota-update/ota-update-routing.module';
import { vcRoutes } from '@home/pages/vc/vc-routing.module';
import { scheduleRoutes } from '../scheduler/scheduler-routing.module';

const routes: Routes = [
  {
    path: 'features',
    data: {
      auth: [Authority.TENANT_ADMIN],
      breadcrumb: {
        label: 'feature.advanced-features',
        icon: 'construction'
      }
    },
    children: [
      {
        path: '',
        children: [],
        data: {
          auth: [Authority.TENANT_ADMIN],
          redirectTo: '/features/otaUpdates'
        }
      },
      ...otaUpdatesRoutes,
      ...vcRoutes,
      ...scheduleRoutes
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FeaturesRoutingModule { }
