import { TenantId } from './id/tenant-id';
import { BaseData, HasId } from '@shared/models/base-data';

export enum EntityType {
  TENANT = 'TENANT',
  TENANT_PROFILE = 'TENANT_PROFILE',
  CUSTOMER = 'CUSTOMER',
  USER = 'USER',
  DASHBOARD = 'DASHBOARD',
  ASSET = 'ASSET',
  DEVICE = 'DEVICE',
  DEVICE_PROFILE = 'DEVICE_PROFILE',
  ASSET_PROFILE = 'ASSET_PROFILE',
  ALARM = 'ALARM',
  RULE_CHAIN = 'RULE_CHAIN',
  RULE_NODE = 'RULE_NODE',
  EDGE = 'EDGE',
  ENTITY_VIEW = 'ENTITY_VIEW',
  WIDGETS_BUNDLE = 'WIDGETS_BUNDLE',
  WIDGET_TYPE = 'WIDGET_TYPE',
  API_USAGE_STATE = 'API_USAGE_STATE',
  TB_RESOURCE = 'TB_RESOURCE',
  OTA_PACKAGE = 'OTA_PACKAGE',
  RPC = 'RPC',
  QUEUE = 'QUEUE',
  NOTIFICATION = 'NOTIFICATION',
  NOTIFICATION_REQUEST = 'NOTIFICATION_REQUEST',
  NOTIFICATION_RULE = 'NOTIFICATION_RULE',
  NOTIFICATION_TARGET = 'NOTIFICATION_TARGET',
  NOTIFICATION_TEMPLATE = 'NOTIFICATION_TEMPLATE',
  REPORT = 'REPORT',
  SCHEDULERS = 'SCHEDULERS',
  SCHEDULERS_PROFILE = 'SCHEDULERS_PROFILE',
  VEHICLE = 'VEHICLE',
  DRIVER = 'DRIVER',
  TRIP = 'TRIP',
  TRIP_HISTORY='TRIP_HISTORY'
}

export enum AliasEntityType {
  CURRENT_CUSTOMER = 'CURRENT_CUSTOMER',
  CURRENT_TENANT = 'CURRENT_TENANT',
  CURRENT_USER = 'CURRENT_USER',
  CURRENT_USER_OWNER = 'CURRENT_USER_OWNER'
}

export interface EntityTypeTranslation {
  type?: string;
  typePlural?: string;
  list?: string;
  nameStartsWith?: string;
  details?: string;
  add?: string;
  noEntities?: string;
  selectedEntities?: string;
  search?: string;
  update?: string;
}

export interface EntityTypeResource<T> {
  helpLinkId: string;
  helpLinkIdForEntity?(entity: T): string;
}

export const entityTypeTranslations = new Map<EntityType | AliasEntityType, EntityTypeTranslation>(
  [
    [
      EntityType.TENANT,
      {
        type: 'entity.type-tenant',
        typePlural: 'entity.type-tenants',
        list: 'entity.list-of-tenants',
        nameStartsWith: 'entity.tenant-name-starts-with',
        details: 'tenant.tenant-details',
        add: 'tenant.add',
        noEntities: 'tenant.no-tenants-text',
        search: 'tenant.search',
        selectedEntities: 'tenant.selected-tenants'
      }
    ],
    [
      EntityType.TENANT_PROFILE,
      {
        type: 'entity.type-tenant-profile',
        typePlural: 'entity.type-tenant-profiles',
        list: 'entity.list-of-tenant-profiles',
        nameStartsWith: 'entity.tenant-profile-name-starts-with',
        details: 'tenant-profile.tenant-profile-details',
        add: 'tenant-profile.add',
        noEntities: 'tenant-profile.no-tenant-profiles-text',
        search: 'tenant-profile.search',
        selectedEntities: 'tenant-profile.selected-tenant-profiles'
      }
    ],
    [
      EntityType.VEHICLE,
      {
        type: 'entity.type-vehicle',
        typePlural: 'entity.type-vehicles',
        list: 'entity.list-of-vehicles',
        nameStartsWith: 'entity.vehicle-name-starts-with',
        add: 'vehicle.add',
        noEntities: 'vehicle.no-vehicle-text',
        search: 'vehicle.search',
        selectedEntities: 'vehicle.selected-vehicles'
      }
    ],
    [
      EntityType.DRIVER,
      {
        type: 'entity.type-driver',
        typePlural: 'entity.type-drivers',
        list: 'entity.list-of-drivers',
        nameStartsWith: 'entity.driver-name-starts-with',
        add: 'driver.add',
        noEntities: 'driver.no-driver-text',
        search: 'driver.search',
        selectedEntities: 'driver.selected-drivers'
      }
    ],
    [
      EntityType.TRIP,
      {
        type: 'entity.type-trip',
        typePlural: 'entity.type-trips',
        list: 'entity.list-of-trips',
        nameStartsWith: 'entity.trip-name-starts-with',
        details: 'trip.trip-details',
        add: 'trip.add',
        noEntities: 'trip.no-trip-text',
        search: 'trip.search',
        selectedEntities: 'trip.selected-trips'
      }
    ],
    [
      EntityType.CUSTOMER,
      {
        type: 'entity.type-customer',
        typePlural: 'entity.type-customers',
        list: 'entity.list-of-customers',
        nameStartsWith: 'entity.customer-name-starts-with',
        details: 'customer.customer-details',
        add: 'customer.add',
        noEntities: 'customer.no-customers-text',
        search: 'customer.search',
        selectedEntities: 'customer.selected-customers'
      }
    ],
    [
      EntityType.TRIP_HISTORY,
      {
        type: 'entity.type-trip',
        typePlural: 'entity.type-trips',
        list: 'entity.list-of-trips',
        nameStartsWith: 'entity.trip-name-starts-with',
        details: 'trip-history.trip-details',
        // add: 'trip.add',
        noEntities: 'trip-history.no-trip-text',
        search: 'trip-history.search',
        selectedEntities: 'trip-history.selected-trips'
      }
    ],
    [
      EntityType.USER,
      {
        type: 'entity.type-user',
        typePlural: 'entity.type-users',
        list: 'entity.list-of-users',
        nameStartsWith: 'entity.user-name-starts-with',
        details: 'user.user-details',
        add: 'user.add',
        noEntities: 'user.no-users-text',
        search: 'user.search',
        selectedEntities: 'user.selected-users'
      }
    ],
    [
      EntityType.DEVICE,
      {
        type: 'entity.type-device',
        typePlural: 'entity.type-devices',
        list: 'entity.list-of-devices',
        nameStartsWith: 'entity.device-name-starts-with',
        details: 'device.device-details',
        add: 'device.add',
        noEntities: 'device.no-devices-text',
        search: 'device.search',
        selectedEntities: 'device.selected-devices'
      }
    ],
    [
      EntityType.DEVICE_PROFILE,
      {
        type: 'entity.type-device-profile',
        typePlural: 'entity.type-device-profiles',
        list: 'entity.list-of-device-profiles',
        nameStartsWith: 'entity.device-profile-name-starts-with',
        details: 'device-profile.device-profile-details',
        add: 'device-profile.add',
        noEntities: 'device-profile.no-device-profiles-text',
        search: 'device-profile.search',
        selectedEntities: 'device-profile.selected-device-profiles'
      }
    ],
    [
      EntityType.ASSET_PROFILE,
      {
        type: 'entity.type-asset-profile',
        typePlural: 'entity.type-asset-profiles',
        list: 'entity.list-of-asset-profiles',
        nameStartsWith: 'entity.asset-profile-name-starts-with',
        details: 'asset-profile.asset-profile-details',
        add: 'asset-profile.add',
        noEntities: 'asset-profile.no-asset-profiles-text',
        search: 'asset-profile.search',
        selectedEntities: 'asset-profile.selected-asset-profiles'
      }
    ],
    [
      EntityType.ASSET,
      {
        type: 'entity.type-asset',
        typePlural: 'entity.type-assets',
        list: 'entity.list-of-assets',
        nameStartsWith: 'entity.asset-name-starts-with',
        details: 'asset.asset-details',
        add: 'asset.add',
        noEntities: 'asset.no-assets-text',
        search: 'asset.search',
        selectedEntities: 'asset.selected-assets'
      }
    ],
    [
      EntityType.SCHEDULERS,
      {
        type: 'entity.type-schedulers',
        typePlural: 'entity.type-scheduler',
        list: 'entity.list-of-scheduler',
        nameStartsWith: 'entity.schedulers-name-starts-with',
        add: 'schedulers.add',
        noEntities: 'schedulers.no-scheduler-text',
        search: 'schedulers.search',
        selectedEntities: 'schedulers.selected-scheduler',
        update:'schedulers.update'
      }
    ],
    [
      EntityType.EDGE,
      {
        type: 'entity.type-edge',
        typePlural: 'entity.type-edges',
        list: 'entity.list-of-edges',
        nameStartsWith: 'entity.edge-name-starts-with',
        details: 'edge.edge-details',
        add: 'edge.add',
        noEntities: 'edge.no-edges-text',
        search: 'edge.search',
        selectedEntities: 'edge.selected-edges'
      }
    ],
    [
      EntityType.ENTITY_VIEW,
      {
        type: 'entity.type-entity-view',
        typePlural: 'entity.type-entity-views',
        list: 'entity.list-of-entity-views',
        nameStartsWith: 'entity.entity-view-name-starts-with',
        details: 'entity-view.entity-view-details',
        add: 'entity-view.add',
        noEntities: 'entity-view.no-entity-views-text',
        search: 'entity-view.search',
        selectedEntities: 'entity-view.selected-entity-views'
      }
    ],
    [
      EntityType.RULE_CHAIN,
      {
        type: 'entity.type-rulechain',
        typePlural: 'entity.type-rulechains',
        list: 'entity.list-of-rulechains',
        nameStartsWith: 'entity.rulechain-name-starts-with',
        details: 'rulechain.rulechain-details',
        add: 'rulechain.add',
        noEntities: 'rulechain.no-rulechains-text',
        search: 'rulechain.search',
        selectedEntities: 'rulechain.selected-rulechains'
      }
    ],
    [
      EntityType.RULE_NODE,
      {
        type: 'entity.type-rulenode',
        typePlural: 'entity.type-rulenodes',
        list: 'entity.list-of-rulenodes',
        nameStartsWith: 'entity.rulenode-name-starts-with'
      }
    ],
    [
      EntityType.DASHBOARD,
      {
        type: 'entity.type-dashboard',
        typePlural: 'entity.type-dashboards',
        list: 'entity.list-of-dashboards',
        nameStartsWith: 'entity.dashboard-name-starts-with',
        details: 'dashboard.dashboard-details',
        add: 'dashboard.add',
        noEntities: 'dashboard.no-dashboards-text',
        search: 'dashboard.search',
        selectedEntities: 'dashboard.selected-dashboards'
      }
    ],
    [
      EntityType.REPORT,
      {
        type: 'entity.type-report',
        typePlural: 'entity.type-reports',
        list: 'entity.list-of-reports',
        nameStartsWith: 'entity.report-name-starts-with',
        details: 'report.report-details',
        add: 'report.add',
        noEntities: 'report.no-reports-text',
        search: 'report.search',
        selectedEntities: 'report.selected-reports'
      }
    ],
    [
      EntityType.ALARM,
      {
        type: 'entity.type-alarm',
        typePlural: 'entity.type-alarms',
        list: 'entity.list-of-alarms',
        nameStartsWith: 'entity.alarm-name-starts-with',
        details: 'alarm.alarm-details',
        noEntities: 'alarm.no-alarms-prompt',
        search: 'alarm.search',
        selectedEntities: 'alarm.selected-alarms'
      }
    ],
    [
      EntityType.API_USAGE_STATE,
      {
        type: 'entity.type-api-usage-state'
      }
    ],
    [
      EntityType.WIDGET_TYPE,
      {
        type: 'entity.type-widget',
        typePlural: 'entity.type-widgets',
        list: 'entity.list-of-widgets',
        details: 'widget.details',
        add: 'dashboard.add-widget',
        noEntities: 'widget.no-widgets-text',
        search: 'widget.search-widgets',
        selectedEntities: 'widget.selected-widgets'
      }
    ],
    [
      EntityType.WIDGETS_BUNDLE,
      {
        type: 'entity.type-widgets-bundle',
        typePlural: 'entity.type-widgets-bundles',
        list: 'entity.list-of-widgets-bundles',
        details: 'widgets-bundle.widgets-bundle-details',
        add: 'widgets-bundle.add',
        noEntities: 'widgets-bundle.no-widgets-bundles-text',
        search: 'widgets-bundle.search',
        selectedEntities: 'widgets-bundle.selected-widgets-bundles'
      }
    ],
    [
      AliasEntityType.CURRENT_CUSTOMER,
      {
        type: 'entity.type-current-customer',
        list: 'entity.type-current-customer'
      }
    ],
    [
      AliasEntityType.CURRENT_TENANT,
      {
        type: 'entity.type-current-tenant',
        list: 'entity.type-current-tenant'
      }
    ],
    [
      AliasEntityType.CURRENT_USER,
      {
        type: 'entity.type-current-user',
        list: 'entity.type-current-user'
      }
    ],
    [
      AliasEntityType.CURRENT_USER_OWNER,
      {
        type: 'entity.type-current-user-owner',
        list: 'entity.type-current-user-owner'
      }
    ],
    [
      EntityType.TB_RESOURCE,
      {
        type: 'entity.type-tb-resource',
        typePlural: 'entity.type-tb-resources',
        list: 'entity.list-of-tb-resources',
        details: 'resource.resource-library-details',
        add: 'resource.add',
        noEntities: 'resource.no-resource-text',
        search: 'resource.search',
        selectedEntities: 'resource.selected-resources'
      }
    ],
    [
      EntityType.OTA_PACKAGE,
      {
        type: 'entity.type-ota-package',
        details: 'ota-update.ota-update-details',
        add: 'ota-update.add',
        noEntities: 'ota-update.no-packages-text',
        search: 'ota-update.search',
        selectedEntities: 'ota-update.selected-package'
      }
    ],
    [
      EntityType.RPC,
      {
        type: 'entity.type-rpc'
      }
    ],
    [
      EntityType.QUEUE,
      {
        type: 'entity.type-queue',
        add: 'queue.add',
        search: 'queue.search',
        details: 'queue.details',
        selectedEntities: 'queue.selected-queues'
      }
    ],
    [
      EntityType.NOTIFICATION,
      {
        type: 'entity.type-notification',
        noEntities: 'notification.no-inbox-notification',
        search: 'notification.search-notification',
        selectedEntities: 'notification.selected-notifications'
      }
    ],
    [
      EntityType.NOTIFICATION_REQUEST,
      {
        type: 'entity.type-notification-request',
        noEntities: 'notification.no-notification-request',
        selectedEntities: 'notification.selected-requests'
      }
    ],
    [
      EntityType.NOTIFICATION_RULE,
      {
        type: 'entity.type-notification-rule',
        typePlural: 'entity.type-notification-rules',
        list: 'entity.list-of-notification-rules',
        noEntities: 'notification.no-rules-notification',
        search: 'notification.search-rules',
        selectedEntities: 'notification.selected-rules'
      }
    ],
    [
      EntityType.NOTIFICATION_TARGET,
      {
        type: 'entity.type-notification-target',
        typePlural: 'entity.type-notification-targets',
        list: 'entity.list-of-notification-targets',
        noEntities: 'notification.no-recipients-notification',
        search: 'notification.search-recipients',
        selectedEntities: 'notification.selected-recipients'
      }
    ],
    [
      EntityType.NOTIFICATION_TEMPLATE,
      {
        type: 'entity.type-notification-template',
        typePlural: 'entity.type-notification-templates',
        list: 'entity.list-of-notification-templates',
        noEntities: 'notification.no-notification-templates',
        search: 'notification.search-templates',
        selectedEntities: 'notification.selected-template'
      }
    ]
  ]
);

export const entityTypeResources = new Map<EntityType, EntityTypeResource<BaseData<HasId>>>(
  [
    [
      EntityType.TENANT,
      {
        helpLinkId: 'tenants'
      }
    ],
    [
      EntityType.TENANT_PROFILE,
      {
        helpLinkId: 'tenantProfiles'
      }
    ],
    [
      EntityType.CUSTOMER,
      {
        helpLinkId: 'customers'
      }
    ],
    [
      EntityType.USER,
      {
        helpLinkId: 'users'
      }
    ],
    [
      EntityType.DEVICE,
      {
        helpLinkId: 'devices'
      }
    ],
    [
      EntityType.DEVICE_PROFILE,
      {
        helpLinkId: 'deviceProfiles'
      }
    ],
    [
      EntityType.ASSET_PROFILE,
      {
        helpLinkId: 'assetProfiles'
      }
    ],
    [
      EntityType.ASSET,
      {
        helpLinkId: 'assets'
      }
    ],
    [
      EntityType.TRIP_HISTORY,
      {
        helpLinkId: 'tripHistory'
      }
    ],
    [
      EntityType.SCHEDULERS,
      {
        helpLinkId: 'schedulers'
      }
    ],
    [
      EntityType.EDGE,
      {
        helpLinkId: 'edges'
      }
    ],
    [
      EntityType.ENTITY_VIEW,
      {
        helpLinkId: 'entityViews'
      }
    ],
    [
      EntityType.RULE_CHAIN,
      {
        helpLinkId: 'rulechains'
      }
    ],
    [
      EntityType.VEHICLE,
      {
        helpLinkId: 'vehicle'
      }
    ],
    [
      EntityType.DASHBOARD,
      {
        helpLinkId: 'dashboards'
      }
    ],
    [
      EntityType.REPORT,
      {
        helpLinkId: 'reports'
      }
    ],
    [
      EntityType.WIDGET_TYPE,
      {
        helpLinkId: 'widgetTypes'
      }
    ],
    [
      EntityType.WIDGETS_BUNDLE,
      {
        helpLinkId: 'widgetsBundles'
      }
    ],
    [
      EntityType.TB_RESOURCE,
      {
        helpLinkId: 'lwm2mResourceLibrary'
      }
    ],
    [
      EntityType.OTA_PACKAGE,
      {
        helpLinkId: 'otaUpdates'
      }
    ],
    [
      EntityType.QUEUE,
      {
        helpLinkId: 'queue'
      }
    ]
  ]
);

export const baseDetailsPageByEntityType = new Map<EntityType, string>([
  [EntityType.TENANT, '/tenants'],
  [EntityType.TENANT_PROFILE, '/tenantProfiles'],
  [EntityType.CUSTOMER, '/customers'],
  [EntityType.USER, '/users'],
  [EntityType.DASHBOARD, '/dashboards'],
  [EntityType.REPORT, '/reports'],
  [EntityType.ASSET, '/entities/assets'],
  [EntityType.DEVICE, '/entities/devices'],
  [EntityType.DEVICE_PROFILE, '/profiles/deviceProfiles'],
  [EntityType.ASSET_PROFILE, '/profiles/assetProfiles'],
  [EntityType.TRIP_HISTORY, '/tripHistory'],
  [EntityType.RULE_CHAIN, '/ruleChains'],
  [EntityType.EDGE, '/edgeManagement/instances'],
  [EntityType.ENTITY_VIEW, '/entities/entityViews'],
  [EntityType.TB_RESOURCE, '/resources/resources-library'],
  [EntityType.OTA_PACKAGE, '/features/otaUpdates'],
  [EntityType.QUEUE, '/settings/queues'],
  [EntityType.WIDGETS_BUNDLE, '/resources/widgets-library/widgets-bundles/details'],
  [EntityType.WIDGET_TYPE, '/resources/widgets-library/widget-types/details'],
  [EntityType.VEHICLE, '/vehicles']
]);

export interface EntitySubtype {
  tenantId: TenantId;
  entityType: EntityType;
  type: string;
}
