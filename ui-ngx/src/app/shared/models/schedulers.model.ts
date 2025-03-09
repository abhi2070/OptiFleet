import { TenantId } from '@shared/models/id/tenant-id';
import { BaseData, ExportableEntity } from '@shared/models/base-data';
import { CustomerId } from '@shared/models/id/customer-id';
import { EntitySearchQuery } from '@shared/models/relation.models';
import { RuleChainId } from '@shared/models/id/rule-chain-id';
import { DashboardId } from '@shared/models/id/dashboard-id';
import { EntityInfoData, HasTenantId } from '@shared/models/entity.models';
import { SchedulersProfileId } from './id/schedulers-profile-id';
import { SchedulersId } from './id/schedulers-id';

export const TB_SERVICE_QUEUE = 'TbServiceQueue';

export interface SchedulersProfile extends BaseData<SchedulersProfileId>, HasTenantId, ExportableEntity<SchedulersProfileId> {
  tenantId?: TenantId;
  name: string;
  description?: string;
  default?: boolean;
  image?: string;
  defaultRuleChainId?: RuleChainId;
  defaultDashboardId?: DashboardId;
  defaultQueueName?: string;
  defaultEdgeRuleChainId?: RuleChainId;
}

export interface SchedulersProfileInfo extends EntityInfoData {
  tenantId?: TenantId;
  image?: string;
  defaultDashboardId?: DashboardId;
}

export interface Schedulers extends BaseData<SchedulersId>, HasTenantId, ExportableEntity<SchedulersId> {
  tenantId?: TenantId;
  customerId?: CustomerId;
  name: string;
  label: string;
  additionalInfo?: string;
  subject: string;
  cc: string;
  bcc: string;
  toAddress: string;
  fromAddress: string;
  reportType: string;
  reportNamePattern: string;
  start: string;
  endDate: string;
  repeatSchedule: string;
  schedule: boolean;
  repeat: boolean;
  timeZone: string;
  body: string;
  active: boolean;
  reportCategory: string;
  reportAvailable: string;
  icon: string; // or appropriate type
  customerTitle: string;
  customerIsPublic: boolean;
}

export interface EmailConfiguration{
  toAddress: string;
  subject: string;
  cc: string;
  bcc: string;
  body: string;
}

export interface SchedulersInfo extends Schedulers {
  icon: string;
  customerTitle: string;
  customerIsPublic: boolean;
}

export interface SchedulersSearchQuery extends EntitySearchQuery {
  SchedulersTypes: Array<string>;
}
