import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { AppState } from '../core.state';
import { getCurrentOpenedMenuSections, selectAuth, selectIsAuthenticated } from '../auth/auth.selectors';
import { filter, map, take } from 'rxjs/operators';
import { HomeSection, MenuSection } from '@core/services/menu.models';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Authority } from '@shared/models/authority.enum';
import { AuthState } from '@core/auth/auth.models';
import { NavigationEnd, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class MenuService {

  currentMenuSections: Array<MenuSection>;
  menuSections$: Subject<Array<MenuSection>> = new BehaviorSubject<Array<MenuSection>>([]);
  homeSections$: Subject<Array<HomeSection>> = new BehaviorSubject<Array<HomeSection>>([]);
  availableMenuLinks$ = this.menuSections$.pipe(
    map((items) => this.allMenuLinks(items))
  );

  constructor(private store: Store<AppState>,
    private router: Router) {
    this.store.pipe(select(selectIsAuthenticated)).subscribe(
      (authenticated: boolean) => {
        if (authenticated) {
          this.buildMenu();
        }
      }
    );
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(
      () => {
        this.updateOpenedMenuSections();
      }
    );
  }

  private buildMenu() {
    this.store.pipe(select(selectAuth), take(1)).subscribe(
      (authState: AuthState) => {
        if (authState.authUser) {
          let homeSections: Array<HomeSection>;
          switch (authState.authUser.authority) {
            case Authority.SYS_ADMIN:
              this.currentMenuSections = this.buildSysAdminMenu();
              homeSections = this.buildSysAdminHome();
              break;
            case Authority.TENANT_ADMIN:
              this.currentMenuSections = this.buildTenantAdminMenu(authState);
              homeSections = this.buildTenantAdminHome(authState);
              break;
            case Authority.CUSTOMER_USER:
              this.currentMenuSections = this.buildCustomerUserMenu(authState);
              homeSections = this.buildCustomerUserHome(authState);
              break;
          }
          this.updateOpenedMenuSections();
          this.menuSections$.next(this.currentMenuSections);
          this.homeSections$.next(homeSections);
        }
      }
    );
  }

  private updateOpenedMenuSections() {
    const url = this.router.url;
    const openedMenuSections = getCurrentOpenedMenuSections(this.store);
    this.currentMenuSections.filter(section => section.type === 'toggle' &&
      (url.startsWith(section.path) || openedMenuSections.includes(section.path))).forEach(
        section => section.opened = true
      );
  }

  private buildSysAdminMenu(): Array<MenuSection> {
    const sections: Array<MenuSection> = [];
    sections.push(
      {
        id: 'home',
        name: 'home.home',
        type: 'link',
        path: '/home',
        icon: 'home'
      },
      {
        id: 'tenants',
        name: 'tenant.tenants',
        type: 'link',
        path: '/tenants',
        icon: 'supervisor_account'
      },
      {
        id: 'tenant_profiles',
        name: 'tenant-profile.tenant-profiles',
        type: 'link',
        path: '/tenantProfiles',
        icon: 'mdi:alpha-t-box'
      },
      {
        id: 'resources',
        name: 'admin.resources',
        type: 'toggle',
        path: '/resources',
        icon: 'folder',
        pages: [
          {
            id: 'widget_library',
            name: 'widget.widget-library',
            type: 'link',
            path: '/resources/widgets-library',
            icon: 'now_widgets',
            pages: [
              {
                id: 'widget_types',
                name: 'widget.widgets',
                type: 'link',
                path: '/resources/widgets-library/widget-types',
                icon: 'now_widgets'
              },
              {
                id: 'widgets_bundles',
                name: 'widgets-bundle.widgets-bundles',
                type: 'link',
                path: '/resources/widgets-library/widgets-bundles',
                icon: 'now_widgets'
              }
            ]
          },
          {
            id: 'images',
            name: 'image.gallery',
            type: 'link',
            path: '/resources/images',
            icon: 'filter'
          },
          {
            id: 'resources_library',
            name: 'resource.resources-library',
            type: 'link',
            path: '/resources/resources-library',
            icon: 'mdi:rhombus-split'
          }
        ]
      },
      {
        id: 'notifications_center',
        name: 'notification.notification-center',
        type: 'link',
        path: '/notification',
        icon: 'mdi:message-badge',
        pages: [
          {
            id: 'notification_inbox',
            name: 'notification.inbox',
            fullName: 'notification.notification-inbox',
            type: 'link',
            path: '/notification/inbox',
            icon: 'inbox'
          },
          {
            id: 'notification_sent',
            name: 'notification.sent',
            fullName: 'notification.notification-sent',
            type: 'link',
            path: '/notification/sent',
            icon: 'outbox'
          },
          {
            id: 'notification_recipients',
            name: 'notification.recipients',
            fullName: 'notification.notification-recipients',
            type: 'link',
            path: '/notification/recipients',
            icon: 'contacts'
          },
          {
            id: 'notification_templates',
            name: 'notification.templates',
            fullName: 'notification.notification-templates',
            type: 'link',
            path: '/notification/templates',
            icon: 'mdi:message-draw'
          },
          {
            id: 'notification_rules',
            name: 'notification.rules',
            fullName: 'notification.notification-rules',
            type: 'link',
            path: '/notification/rules',
            icon: 'mdi:message-cog'
          }
        ]
      },
      {
        id: 'security_settings',
        name: 'security.security',
        type: 'toggle',
        path: '/security-settings',
        icon: 'security',
        pages: [
          {
            id: 'security_settings_general',
            name: 'admin.general',
            fullName: 'security.general-settings',
            type: 'link',
            path: '/security-settings/general',
            icon: 'settings_applications'
          },
          {
            id: '2fa',
            name: 'admin.2fa.2fa',
            type: 'link',
            path: '/security-settings/2fa',
            icon: 'mdi:two-factor-authentication'
          },
          {
            id: 'oauth2',
            name: 'admin.oauth2.oauth2',
            type: 'link',
            path: '/security-settings/oauth2',
            icon: 'mdi:shield-account'
          },
          {
            id: 'settings',
            name: 'admin.settings',
            type: 'link',
            path: '/security-settings/settings',
            icon: 'settings',
            pages: [
              {
                id: 'general',
                name: 'admin.general',
                fullName: 'admin.general-settings',
                type: 'link',
                path: '/security-settings/settings/general',
                icon: 'settings_applications'
              },
              {
                id: 'mail_server',
                name: 'admin.outgoing-mail',
                type: 'link',
                path: '/security-settings/settings/outgoing-mail',
                icon: 'mail'
              },
              {
                id: 'notification_settings',
                name: 'admin.notifications',
                fullName: 'admin.notifications-settings',
                type: 'link',
                path: '/security-settings/settings/notifications',
                icon: 'mdi:message-badge'
              },
              {
                id: 'queues',
                name: 'admin.queues',
                type: 'link',
                path: '/security-settings/settings/queues',
                icon: 'swap_calls'
              },
            ]
          }
        ]
      }
    );
    return sections;
  }

  private buildSysAdminHome(): Array<HomeSection> {
    const homeSections: Array<HomeSection> = [];
    homeSections.push(
      {
        name: 'tenant.management',
        places: [
          {
            name: 'tenant.tenants',
            icon: 'supervisor_account',
            path: '/tenants'
          },
          {
            name: 'tenant-profile.tenant-profiles',
            icon: 'mdi:alpha-t-box',
            path: '/tenantProfiles'
          },
        ]
      },
      {
        name: 'widget.management',
        places: [
          {
            name: 'widget.widget-library',
            icon: 'now_widgets',
            path: '/resources/widgets-library',
          }
        ]
      },
      {
        name: 'admin.system-settings',
        places: [
          {
            name: 'admin.general',
            icon: 'settings_applications',
            path: '/settings/general'
          },
          {
            name: 'admin.outgoing-mail',
            icon: 'mail',
            path: '/settings/outgoing-mail'
          },
          {
            name: 'admin.sms-provider',
            icon: 'sms',
            path: '/settings/sms-provider'
          },
          {
            name: 'admin.security-settings',
            icon: 'security',
            path: '/settings/security-settings'
          },
          {
            name: 'admin.oauth2.oauth2',
            icon: 'security',
            path: '/settings/oauth2'
          },
          {
            name: 'admin.2fa.2fa',
            icon: 'mdi:two-factor-authentication',
            path: '/settings/2fa'
          },
          {
            name: 'resource.resources-library',
            icon: 'folder',
            path: '/settings/resources-library'
          },
          {
            name: 'admin.queues',
            icon: 'swap_calls',
            path: '/settings/queues'
          },
        ]
      }
    );
    return homeSections;
  }

  private buildTenantAdminMenu(authState: AuthState): Array<MenuSection> {
    const sections: Array<MenuSection> = [];
    sections.push(
      {
        id: 'home',
        name: 'home.home',
        type: 'link',
        path: '/home',
        icon: 'home'
      },
      {
        id: 'trip',
        name: 'trip.trips',
        type: 'link',
        path: '/trips',
        icon: 'directions'
      },
      {
        id: 'vehicles',
        name: 'vehicle.vehicles',
        type: 'link',
        path: '/vehicles',
        icon: 'anchor'
      },
      {
        id: 'drivers',
        name: 'driver.drivers',
        type: 'link',
        path: '/drivers',
        icon: 'airline_seat_recline_extra'
      },
      {
        id: 'trip_history',
        name: 'trip-history.trip-historys',
        type: 'link',
        path: '/tripHistory',
        icon: 'history'
      },
      {
        id: 'entities',
        name: 'entity.entities',
        type: 'toggle',
        path: '/entities',
        icon: 'category',
        pages: [
          {
            id: 'devices',
            name: 'device.devices',
            type: 'link',
            path: '/entities/devices',
            icon: 'devices_other'
          },
          {
            id: 'assets',
            name: 'asset.assets',
            type: 'link',
            path: '/entities/assets',
            icon: 'domain'
          }
        ]
      },
      {
        id: 'profiles',
        name: 'profiles.profiles',
        type: 'toggle',
        path: '/profiles',
        icon: 'badge',
        pages: [
          {
            id: 'device_profiles',
            name: 'device-profile.device-profiles',
            type: 'link',
            path: '/profiles/deviceProfiles',
            icon: 'mdi:alpha-d-box'
          },
          {
            id: 'asset_profiles',
            name: 'asset-profile.asset-profiles',
            type: 'link',
            path: '/profiles/assetProfiles',
            icon: 'mdi:alpha-a-box'
          }
        ]
      },
      {
        id: 'rule_chains',
        name: 'rulechain.rulechains',
        type: 'link',
        path: '/ruleChains',
        icon: 'settings_ethernet'
      },
      {
        id: 'alarms',
        name: 'alarm.alarms',
        type: 'link',
        path: '/alarms',
        icon: 'mdi:alert-outline'
      },
    );
    sections.push(
      {
        id: 'features',
        name: 'feature.advanced-features',
        type: 'toggle',
        path: '/features',
        icon: 'construction',
        pages: [
          {
            id: 'schedulers',
            name: 'schedulers.scheduler',
            type: 'link',
            path: '/features/schedulers',
            icon: 'schedule'
          }
        ]
      },
      {
        id: 'security_settings',
        name: 'security.security',
        type: 'toggle',
        path: '/security-settings',
        icon: 'security',
        pages: [
          {
            id: 'audit_log',
            name: 'audit-log.audit-logs',
            type: 'link',
            path: '/security-settings/auditLogs',
            icon: 'track_changes'
          },
          {
            id: 'notifications_center',
            name: 'notification.notification-center',
            type: 'link',
            path: '/security-settings/notification',
            icon: 'mdi:message-badge',
            pages: [
              {
                id: 'notification_inbox',
                name: 'notification.inbox',
                fullName: 'notification.notification-inbox',
                type: 'link',
                path: '/security-settings/notification/inbox',
                icon: 'inbox'
              },
              {
                id: 'notification_sent',
                name: 'notification.sent',
                fullName: 'notification.notification-sent',
                type: 'link',
                path: '/security-settings/notification/sent',
                icon: 'outbox'
              },
              {
                id: 'notification_recipients',
                name: 'notification.recipients',
                fullName: 'notification.notification-recipients',
                type: 'link',
                path: '/security-settings/notification/recipients',
                icon: 'contacts'
              },
              {
                id: 'notification_templates',
                name: 'notification.templates',
                fullName: 'notification.notification-templates',
                type: 'link',
                path: '/security-settings/notification/templates',
                icon: 'mdi:message-draw'
              },
              {
                id: 'notification_rules',
                name: 'notification.rules',
                fullName: 'notification.notification-rules',
                type: 'link',
                path: '/security-settings/notification/rules',
                icon: 'mdi:message-cog'
              }
            ]
          },
        ]
      }
    );
    return sections;
  }

  private buildTenantAdminHome(authState: AuthState): Array<HomeSection> {
    const homeSections: Array<HomeSection> = [];
    homeSections.push(
      {
        name: 'rulechain.management',
        places: [
          {
            name: 'rulechain.rulechains',
            icon: 'settings_ethernet',
            path: '/ruleChains'
          }
        ]
      },
      {
        name: 'customer.management',
        places: [
          {
            name: 'customer.customers',
            icon: 'supervisor_account',
            path: '/clients'
          }
        ]
      },
      {
        name: 'asset.management',
        places: [
          {
            name: 'asset.assets',
            icon: 'domain',
            path: '/assets'
          },
          {
            name: 'asset-profile.asset-profiles',
            icon: 'mdi:alpha-a-box',
            path: '/profiles/assetProfiles'
          }
        ]
      },
      {
        name: 'device.management',
        places: [
          {
            name: 'device.devices',
            icon: 'devices_other',
            path: '/devices'
          },
          {
            name: 'device-profile.device-profiles',
            icon: 'mdi:alpha-d-box',
            path: '/profiles/deviceProfiles'
          },
          {
            name: 'ota-update.ota-updates',
            icon: 'memory',
            path: '/otaUpdates'
          }
        ]
      },
      {
        name: 'entity-view.management',
        places: [
          {
            name: 'entity-view.entity-views',
            icon: 'view_quilt',
            path: '/entityViews'
          }
        ]
      }
    );
    if (authState.edgesSupportEnabled) {
      homeSections.push(
        {
          name: 'edge.management',
          places: [
            {
              name: 'edge.edge-instances',
              icon: 'router',
              path: '/edgeInstances'
            },
            {
              name: 'edge.rulechain-templates',
              icon: 'settings_ethernet',
              path: '/edgeManagement/ruleChains'
            }
          ]
        }
      );
    }
    homeSections.push(
      {
        name: 'dashboard.management',
        places: [
          {
            name: 'widget.widget-library',
            icon: 'now_widgets',
            path: '/widgets-bundles'
          },
          {
            name: 'dashboard.dashboards',
            icon: 'dashboard',
            path: '/dashboards'
          }
        ]
      },
      {
        name: 'version-control.management',
        places: [
          {
            name: 'version-control.version-control',
            icon: 'history',
            path: '/vc'
          }
        ]
      },
      {
        name: 'audit-log.audit',
        places: [
          {
            name: 'audit-log.audit-logs',
            icon: 'track_changes',
            path: '/auditLogs'
          },
          {
            name: 'api-usage.api-usage',
            icon: 'insert_chart',
            path: '/usage'
          }
        ]
      },
      {
        name: 'admin.system-settings',
        places: [
          {
            name: 'admin.home-settings',
            icon: 'settings_applications',
            path: '/settings/home'
          },
          {
            name: 'resource.resources-library',
            icon: 'folder',
            path: '/settings/resources-library'
          },
          {
            name: 'admin.repository-settings',
            icon: 'manage_history',
            path: '/settings/repository',
          },
          {
            name: 'admin.auto-commit-settings',
            icon: 'settings_backup_restore',
            path: '/settings/auto-commit'
          }
        ]
      }
    );
    return homeSections;
  }

  private buildCustomerUserMenu(authState: AuthState): Array<MenuSection> {
    const sections: Array<MenuSection> = [];
    sections.push(
      {
        id: 'home',
        name: 'home.home',
        type: 'link',
        path: '/home',
        icon: 'home'
      },
      {
        id: 'alarms',
        name: 'alarm.alarms',
        type: 'link',
        path: '/alarms',
        icon: 'mdi:alert-outline'
      },
      {
        id: 'dashboards',
        name: 'dashboard.dashboards',
        type: 'link',
        path: '/dashboards',
        icon: 'dashboards'
      },
      {
        id: 'reports',
        name: 'report.reports',
        type: 'link',
        path: '/reports',
        icon: 'file_copy'
      },
      {
        id: 'entities',
        name: 'entity.entities',
        type: 'toggle',
        path: '/entities',
        icon: 'category',
        pages: [
          {
            id: 'devices',
            name: 'device.devices',
            type: 'link',
            path: '/entities/devices',
            icon: 'devices_other'
          },
          {
            id: 'assets',
            name: 'asset.assets',
            type: 'link',
            path: '/entities/assets',
            icon: 'domain'
          },
          {
            id: 'entity_views',
            name: 'entity-view.entity-views',
            type: 'link',
            path: '/entities/entityViews',
            icon: 'view_quilt'
          }
        ]
      }
    );
    if (authState.edgesSupportEnabled) {
      sections.push(
        {
          id: 'edges',
          name: 'edge.edge-instances',
          fullName: 'edge.edge-instances',
          type: 'link',
          path: '/edgeManagement/instances',
          icon: 'router'
        }
      );
    }
    sections.push(
      {
        id: 'notifications_center',
        name: 'notification.notification-center',
        type: 'link',
        path: '/notification',
        icon: 'mdi:message-badge',
        pages: [
          {
            id: 'notification_inbox',
            name: 'notification.inbox',
            fullName: 'notification.notification-inbox',
            type: 'link',
            path: '/notification/inbox',
            icon: 'inbox'
          }
        ]
      }
    );
    return sections;
  }

  private buildCustomerUserHome(authState: AuthState): Array<HomeSection> {
    const homeSections: Array<HomeSection> = [];
    homeSections.push(
      {
        name: 'asset.view-assets',
        places: [
          {
            name: 'asset.assets',
            icon: 'domain',
            path: '/assets'
          }
        ]
      },
      {
        name: 'device.view-devices',
        places: [
          {
            name: 'device.devices',
            icon: 'devices_other',
            path: '/devices'
          }
        ]
      },
      {
        name: 'entity-view.management',
        places: [
          {
            name: 'entity-view.entity-views',
            icon: 'view_quilt',
            path: '/entityViews'
          }
        ]
      }
    );
    if (authState.edgesSupportEnabled) {
      homeSections.push(
        {
          name: 'edge.management',
          places: [
            {
              name: 'edge.edge-instances',
              icon: 'settings_input_antenna',
              path: '/edgeInstances'
            }
          ]
        }
      );
    }
    homeSections.push(
      {
        name: 'dashboard.view-dashboards',
        places: [
          {
            name: 'dashboard.dashboards',
            icon: 'dashboard',
            path: '/dashboards'
          }
        ]
      }
    );
    return homeSections;
  }

  private allMenuLinks(sections: Array<MenuSection>): Array<MenuSection> {
    const result: Array<MenuSection> = [];
    for (const section of sections) {
      if (section.type === 'link') {
        result.push(section);
      }
      if (section.pages && section.pages.length) {
        result.push(...this.allMenuLinks(section.pages));
      }
    }
    return result;
  }

  public menuSections(): Observable<Array<MenuSection>> {
    return this.menuSections$;
  }

  public homeSections(): Observable<Array<HomeSection>> {
    return this.homeSections$;
  }

  public availableMenuLinks(): Observable<Array<MenuSection>> {
    return this.availableMenuLinks$;
  }

  public menuLinkById(id: string): Observable<MenuSection | undefined> {
    return this.availableMenuLinks$.pipe(
      map((links) => links.find(link => link.id === id))
    );
  }

  public menuLinksByIds(ids: string[]): Observable<Array<MenuSection>> {
    return this.availableMenuLinks$.pipe(
      map((links) => links.filter(link => ids.includes(link.id)).sort((a, b) => {
        const i1 = ids.indexOf(a.id);
        const i2 = ids.indexOf(b.id);
        return i1 - i2;
      }))
    );
  }

}
