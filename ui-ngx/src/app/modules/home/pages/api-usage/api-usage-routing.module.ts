

import { inject, NgModule } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, RouterModule, RouterStateSnapshot, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { ApiUsageComponent } from '@home/pages/api-usage/api-usage.component';
import { Dashboard } from '@shared/models/dashboard.models';
import { ResourcesService } from '@core/services/resources.service';
import { Observable } from 'rxjs';

const apiUsageDashboardJson = '/assets/dashboard/api_usage.json';

export const apiUsageDashboardResolver: ResolveFn<Dashboard> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
  resourcesService = inject(ResourcesService)
): Observable<Dashboard> => resourcesService.loadJsonResource(apiUsageDashboardJson);

export const apiUsageRoutes: Routes = [
  {
    path: 'usage',
    component: ApiUsageComponent,
    data: {
      auth: [Authority.TENANT_ADMIN],
      title: 'api-usage.api-usage',
      breadcrumb: {
        label: 'api-usage.api-usage',
        icon: 'insert_chart'
      }
    },
    resolve: {
      apiUsageDashboard: apiUsageDashboardResolver
    }
  }
];

const routes: Routes = [
  {
    path: 'usage',
    redirectTo: '/security-settings/usage',
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ApiUsageRoutingModule { }
