

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { AuditLogTableComponent } from '@home/components/audit-log/audit-log-table.component';

export const auditLogsRoutes: Routes = [
  {
    path: 'auditLogs',
    component: AuditLogTableComponent,
    data: {
      auth: [Authority.TENANT_ADMIN],
      title: 'audit-log.audit-logs',
      breadcrumb: {
        label: 'audit-log.audit-logs',
        icon: 'track_changes'
      },
      isPage: true
    }
  }
];

const routes: Routes = [
  {
    path: 'auditLogs',
    redirectTo: '/security-settings/auditLogs'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: []
})
export class AuditLogRoutingModule { }
