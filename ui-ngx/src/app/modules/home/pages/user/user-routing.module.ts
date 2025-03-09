import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { AllUsersTableConfigResolver } from './all-users-table-config.resolver';

const routes: Routes = [
  {
    path: 'users',
    data: {
      breadcrumb: {
        label: 'user.users',
        icon: 'account_circle'
      }
    },
    children: [
      {
        path: '',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'user.users'
        },
        resolve: {
          entitiesTableConfig: AllUsersTableConfigResolver
        }
      },
      // {
      //   path: ':entityId',
      //   component: EntityDetailsPageComponent,
      //   canDeactivate: [ConfirmOnExitGuard],
      //   data: {
      //     breadcrumb: {
      //       labelFunction: entityDetailsPageBreadcrumbLabelFunction,
      //       icon: 'account_circle'
      //     } as BreadCrumbConfig<EntityDetailsPageComponent>,
      //     auth: [Authority.SYS_ADMIN, Authority.TENANT_ADMIN],
      //     title: 'user.user',
      //   },
      //   resolve: {
      //     entitiesTableConfig: UsersTableConfigResolver
      //   }
      // }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    AllUsersTableConfigResolver
  ]
})
export class UserRoutingModule { }
