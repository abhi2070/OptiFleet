

import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { NgModule } from '@angular/core';
import { RouterTabsComponent } from '@home/components/router-tabs.component';
import { EntitiesTableComponent } from '@home/components/entity/entities-table.component';
import { InboxTableConfigResolver } from '@home/pages/notification/inbox/inbox-table-config.resolver';
import { SentTableConfigResolver } from '@home/pages/notification/sent/sent-table-config.resolver';
import { RecipientTableConfigResolver } from '@home/pages/notification/recipient/recipient-table-config.resolver';
import { TemplateTableConfigResolver } from '@home/pages/notification/template/template-table-config.resolver';
import { RuleTableConfigResolver } from '@home/pages/notification/rule/rule-table-config.resolver';
import { SendNotificationButtonComponent } from '@home/components/notification/send-notification-button.component';

export const notificationRoutes: Routes = [
  {
    path: 'notification',
    component: RouterTabsComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER, Authority.SYS_ADMIN],
      breadcrumb: {
        label: 'notification.notification-center',
        icon: 'mdi:message-badge'
      },
      routerTabsHeaderComponent: SendNotificationButtonComponent
    },
    children: [
      {
        path: '',
        children: [],
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER, Authority.SYS_ADMIN],
          redirectTo: '/notification/inbox'
        }
      },
      {
        path: 'inbox',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER, Authority.SYS_ADMIN],
          title: 'notification.inbox',
          breadcrumb: {
            label: 'notification.inbox',
            icon: 'inbox'
          }
        },
        resolve: {
          entitiesTableConfig: InboxTableConfigResolver
        }
      },
      {
        path: 'sent',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.SYS_ADMIN],
          title: 'notification.sent',
          breadcrumb: {
            label: 'notification.sent',
            icon: 'outbox'
          }
        },
        resolve: {
          entitiesTableConfig: SentTableConfigResolver
        }
      },
      {
        path: 'templates',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.SYS_ADMIN],
          title: 'notification.templates',
          breadcrumb: {
            label: 'notification.templates',
            icon: 'mdi:message-draw'
          }
        },
        resolve: {
          entitiesTableConfig: TemplateTableConfigResolver
        }
      },
      {
        path: 'recipients',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.SYS_ADMIN],
          title: 'notification.recipients',
          breadcrumb: {
            label: 'notification.recipients',
            icon: 'contacts'
          },
        },
        resolve: {
          entitiesTableConfig: RecipientTableConfigResolver
        }
      },
      {
        path: 'rules',
        component: EntitiesTableComponent,
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.SYS_ADMIN],
          title: 'notification.rules',
          breadcrumb: {
            label: 'notification.rules',
            icon: 'mdi:message-cog'
          }
        },
        resolve: {
          entitiesTableConfig: RuleTableConfigResolver
        }
      }
    ]
  }
];

const routes: Routes = [
  {
    path: 'notification',
    redirectTo: '/security-settings/notification/inbox',
  }
];

@NgModule({
  providers: [
    InboxTableConfigResolver,
    SentTableConfigResolver,
    RecipientTableConfigResolver,
    TemplateTableConfigResolver,
    RuleTableConfigResolver
  ],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NotificationRoutingModule { }
