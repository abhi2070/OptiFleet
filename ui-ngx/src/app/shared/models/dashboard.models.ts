import { BaseData, ExportableEntity } from '@shared/models/base-data';
import { DashboardId } from '@shared/models/id/dashboard-id';
import { TenantId } from '@shared/models/id/tenant-id';
import { ShortCustomerInfo } from '@shared/models/customer.model';
import { Widget } from './widget.models';
import { Timewindow } from '@shared/models/time/time.models';
import { EntityAliases } from './alias.models';
import { Filters } from '@shared/models/query/query.models';
import { MatDialogRef } from '@angular/material/dialog';
import { HasTenantId } from '@shared/models/entity.models';

export interface DashboardInfo extends BaseData<DashboardId>, HasTenantId, ExportableEntity<DashboardId> {
  tenantId?: TenantId;
  title?: string;
  image?: string;
  assignedCustomers?: Array<ShortCustomerInfo>;
  mobileHide?: boolean;
  mobileOrder?: number;
}

export interface WidgetLayout {
  sizeX?: number;
  sizeY?: number;
  desktopHide?: boolean;
  mobileHide?: boolean;
  mobileHeight?: number;
  mobileOrder?: number;
  col?: number;
  row?: number;
}

export interface WidgetLayouts {
  [id: string]: WidgetLayout;
}

export interface GridSettings {
  backgroundColor?: string;
  columns?: number;
  margin?: number;
  outerMargin?: boolean;
  backgroundSizeMode?: string;
  backgroundImageUrl?: string;
  autoFillHeight?: boolean;
  mobileAutoFillHeight?: boolean;
  mobileRowHeight?: number;
  layoutDimension?: LayoutDimension;
  [key: string]: any;
}

export interface DashboardLayout {
  widgets: WidgetLayouts;
  gridSettings: GridSettings;
}

export interface DashboardLayoutInfo {
  widgetIds?: string[];
  widgetLayouts?: WidgetLayouts;
  gridSettings?: GridSettings;
}

export interface LayoutDimension {
  type?: LayoutType;
  fixedWidth?: number;
  fixedLayout?: DashboardLayoutId;
  leftWidthPercentage?: number;
}

export declare type DashboardLayoutId = 'main' | 'right';

export declare type LayoutType = 'percentage' | 'fixed';

export declare type DashboardStateLayouts = {[key in DashboardLayoutId]?: DashboardLayout};

export declare type DashboardLayoutsInfo = {[key in DashboardLayoutId]?: DashboardLayoutInfo};

export interface DashboardState {
  name: string;
  root: boolean;
  layouts: DashboardStateLayouts;
}

export declare type StateControllerId = 'entity' | 'default' | string;

export interface DashboardSettings {
  stateControllerId?: StateControllerId;
  showTitle?: boolean;
  showDashboardsSelect?: boolean;
  showEntitiesSelect?: boolean;
  showFilters?: boolean;
  showDashboardLogo?: boolean;
  dashboardLogoUrl?: string;
  showDashboardTimewindow?: boolean;
  showDashboardExport?: boolean;
  showUpdateDashboardImage?: boolean;
  toolbarAlwaysOpen?: boolean;
  hideToolbar?: boolean;
  titleColor?: string;
  dashboardCss?: string;
}

export interface DashboardConfiguration {
  timewindow?: Timewindow;
  settings?: DashboardSettings;
  widgets?: {[id: string]: Widget } | Widget[];
  states?: {[id: string]: DashboardState };
  entityAliases?: EntityAliases;
  filters?: Filters;
  [key: string]: any;
}

export interface Dashboard extends DashboardInfo {
  configuration?: DashboardConfiguration;
  dialogRef?: MatDialogRef<any>;
}

export interface HomeDashboard extends Dashboard {
  hideDashboardToolbar: boolean;
}

export interface HomeDashboardInfo {
  dashboardId: DashboardId;
  hideDashboardToolbar: boolean;
}

export interface DashboardSetup extends Dashboard {
  assignedCustomerIds?: Array<string>;
}

export const isPublicDashboard = (dashboard: DashboardInfo): boolean => {
  if (dashboard && dashboard.assignedCustomers) {
    return dashboard.assignedCustomers
      .filter(customerInfo => customerInfo.public).length > 0;
  } else {
    return false;
  }
};

export const getDashboardAssignedCustomersText = (dashboard: DashboardInfo): string => {
  if (dashboard && dashboard.assignedCustomers && dashboard.assignedCustomers.length > 0) {
    return dashboard.assignedCustomers
      .filter(customerInfo => !customerInfo.public)
      .map(customerInfo => customerInfo.title)
      .join(', ');
  } else {
    return '';
  }
};

export const isCurrentPublicDashboardCustomer = (dashboard: DashboardInfo, customerId: string): boolean => {
  if (customerId && dashboard && dashboard.assignedCustomers) {
    return dashboard.assignedCustomers.filter(customerInfo =>
      customerInfo.public && customerId === customerInfo.customerId.id).length > 0;
  } else {
    return false;
  }
};
