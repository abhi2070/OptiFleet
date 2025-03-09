

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './home.component';
import { SharedModule } from '@app/shared/shared.module';
import { MenuLinkComponent } from '@modules/home/menu/menu-link.component';
import { MenuToggleComponent } from '@modules/home/menu/menu-toggle.component';
import { SideMenuComponent } from '@modules/home/menu/side-menu.component';
import { NotificationBellComponent } from '@home/components/notification/notification-bell.component';
import { ShowNotificationPopoverComponent } from '@home/components/notification/show-notification-popover.component';
import { HomePageWidgetsModule } from './components/widget/lib/home-page/home-page-widgets.module';
import { WidgetComponentsModule } from './components/widget/widget-components.module';
import { WidgetLibraryModule } from './pages/widget/widget-library.module';
import { HomeComponentsModule } from './components/home-components.module';

@NgModule({
  declarations:
    [
      HomeComponent,
      MenuLinkComponent,
      MenuToggleComponent,
      SideMenuComponent,
      NotificationBellComponent,
      ShowNotificationPopoverComponent,

    ],
  imports: [
    CommonModule,
    SharedModule,
    HomeRoutingModule,
    HomePageWidgetsModule,
    WidgetLibraryModule,
    HomeComponentsModule
]
})
export class HomeModule { }
