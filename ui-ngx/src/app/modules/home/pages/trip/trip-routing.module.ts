import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { TripComponent } from './trip.component';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'trips',
        component: TripComponent,
        data: {
          breadcrumb: {
            label: 'trip.trips',
            icon: 'directions'
          },
          auth: [Authority.TENANT_ADMIN],
          title: 'trip.trips'
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TripRoutingModule { }