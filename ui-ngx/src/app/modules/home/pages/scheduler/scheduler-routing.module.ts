import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@app/shared/public-api';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { SchedulerTableConfigResolver } from './scheduler-table-config.resolver';

export const scheduleRoutes: Routes = [
  {
    path: 'schedulers',
    data: {
      breadcrumb: {
        label: 'schedulers.scheduler',
        icon: 'schedule'
      }
    },
    children: [
      {
        path: '',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'schedulers.scheduler',
          schedulerType: 'tenant'
        },
        resolve: {
          entitiesTableConfig: SchedulerTableConfigResolver
        }
      },
    ]
  }
];

const routes: Routes = [
  {
    path: 'schedulers',
    redirectTo: '/features/schedulers'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [SchedulerTableConfigResolver]
})
export class SchedulerRoutingModule { }
