import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { EntityDetailsPageComponent } from '../../components/entity/entity-details-page.component';
import { DriversTableConfigResolver } from '@modules/home/pages/driver/drivers-table-config.resolver';
import { ConfirmOnExitGuard } from '@app/core/guards/confirm-on-exit.guard';
import { entityDetailsPageBreadcrumbLabelFunction } from '../home-pages.models';
import { BreadCrumbConfig } from '@app/shared/components/breadcrumb';

const routes: Routes = [
  {
    path: 'drivers',
    data: {
      breadcrumb: {
        label: 'driver.drivers',
        icon: 'airline_seat_recline_extra'
      }
    },
    children: [
      {
        path: '',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'driver.drivers',
          driverType: 'tenant'
        },
        resolve: {
          entitiesTableConfig: DriversTableConfigResolver
        }
      },
      {
        path: ':entityId',
        component: EntityDetailsPageComponent,
        canDeactivate: [ConfirmOnExitGuard],
        data: {
          breadcrumb: {
            labelFunction: entityDetailsPageBreadcrumbLabelFunction,
            icon: 'domain'
          } as BreadCrumbConfig<EntityDetailsPageComponent>,
          auth: [Authority.TENANT_ADMIN],
          title: 'driver.drivers',
          driverType: 'tenant'
        },
        resolve: {
          entitiesTableConfig: DriversTableConfigResolver
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [DriversTableConfigResolver]
})
export class DriverRoutingModule { }
