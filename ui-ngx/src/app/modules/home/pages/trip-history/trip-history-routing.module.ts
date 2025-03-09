import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@app/shared/models/authority.enum';
import { TripHistoryComponent } from './trip-history.component';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'tripHistory',
        component: TripHistoryComponent,
        data: {
          breadcrumb: {
            label: 'trip-history.trip-historys',
            icon: 'history'
          },
          auth: [Authority.TENANT_ADMIN],
          title: 'trip-history.trip-historys'
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TripHistoryRoutingModule { }