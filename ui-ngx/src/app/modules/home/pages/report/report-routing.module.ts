import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { NgModule } from '@angular/core';
import { ReportsTableConfigResolver } from './reports-table-config.resolver';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';

const routes: Routes = [
  {
    path: 'reports',
    data: {
      breadcrumb: {
        label: 'report.reports',
        icon: 'file_copy'
      }
    },
    children: [
      {
        path: '',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
          title: 'report.reports',
          reportsType: 'tenant'
        },
        resolve: {
          entitiesTableConfig: ReportsTableConfigResolver
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    ReportsTableConfigResolver
  ]
})
export class ReportRoutingModule { }
