import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { Authority } from '@app/shared/public-api';
import { VehicleTableConfigResolver } from './vehicle-table-config.resolver';

const routes: Routes = [
  {
    path: 'vehicles',
    data: {
      breadcrumb: {
        label:'vehicle.vehicles',
        icon:'anchor'
      }
    },
    children: [
      {
        path: '',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'vehicle.vehicles',
          vehicleType: 'tenant'
        },
        resolve: {
          entitiesTableConfig: VehicleTableConfigResolver
        }
      },
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers:[
    VehicleTableConfigResolver
  ]
})
export class VehicleRoutingModule { }
