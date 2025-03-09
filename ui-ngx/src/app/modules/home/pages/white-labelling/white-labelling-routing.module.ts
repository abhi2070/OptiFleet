import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@app/shared/public-api';
import { GeneralComponent } from '../../components/white-labelling/general.component';
import { ConfirmOnExitGuard } from '@app/core/guards/confirm-on-exit.guard';
import { LoginComponent } from '../../components/white-labelling/login.component';
import { MailTemplatesComponent } from '../../components/white-labelling/mail-templates.component';
import { CustomTranslationComponent } from '../../components/white-labelling/custom-translation.component';
import { CustomMenuComponent } from '../../components/white-labelling/custom-menu.component';
import { RouterTabsComponent } from '../../components/router-tabs.component';

export const whiteLabellingRoutes: Routes = [
  {
    path: 'white-labelling',
    component: RouterTabsComponent,
    data: {
      auth: [Authority.SYS_ADMIN, Authority.TENANT_ADMIN],
      title: 'white-labelling.white-label',
      breadcrumb: {
        label: 'white-labelling.white-label',
        icon: 'format_paint'
      }
    },
    children: [
      {
        path: '',
        children: [],
        data: {
          auth: [Authority.SYS_ADMIN, Authority.TENANT_ADMIN],
          redirectTo: '/white-labelling/general'
        }
      },
      {
        path: 'general',
        component: GeneralComponent,
        canDeactivate: [ConfirmOnExitGuard],
        data: {
          auth: [Authority.SYS_ADMIN, Authority.TENANT_ADMIN],
          title: 'white-labelling.general',
          breadcrumb: {
            label: 'white-labelling.general',
            icon: 'settings_applications'
          }
        }
      },
      {
        path: 'login',
        component: LoginComponent,
        canDeactivate: [ConfirmOnExitGuard],
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'white-labelling.login',
          breadcrumb: {
            label: 'white-labelling.login',
            icon: 'settings_applications'
          }
        }
      },
      {
        path: 'mail-templates',
        component: MailTemplatesComponent,
        canDeactivate: [ConfirmOnExitGuard],
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'white-labelling.mail-templates',
          breadcrumb: {
            label: 'white-labelling.mail-templates',
            icon: 'settings_applications'
          }
        }
      },
      {
        path: 'custom-translation',
        component: CustomTranslationComponent,
        canDeactivate: [ConfirmOnExitGuard],
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'white-labelling.custom-translation',
          breadcrumb: {
            label: 'white-labelling.custom-translation',
            icon: 'settings_applications'
          }
        }
      },
      {
        path: 'custom-menu',
        component: CustomMenuComponent,
        canDeactivate: [ConfirmOnExitGuard],
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'white-labelling.custom-menu',
          breadcrumb: {
            label: 'white-labelling.custom-menu',
            icon: 'settings_applications'
          }
        }
      },

    ]
  },
];

const routes: Routes = [
  {
    path: 'white-labelling',
    redirectTo: '/security-settings/white-labelling/general',
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WhiteLabellingRoutingModule { }
