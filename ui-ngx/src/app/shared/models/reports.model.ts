import { BaseData, ExportableEntity } from '@shared/models/base-data';
import { ReportsId } from './id/reports-id';
import { HasTenantId } from '@shared/models/entity.models';
import { TenantId } from '@shared/models/id/tenant-id';
import { EntitySearchQuery } from '@shared/models/relation.models';
import { CustomerId } from '@shared/models/id/customer-id';

export interface Reports extends BaseData<ReportsId>, HasTenantId, ExportableEntity<ReportsId> {
tenantID?: TenantId;
customerID?: CustomerId;
title?: string;
name?: string;
type?: string;
}

export interface ReportsInfo extends Reports {
icon: string;
assignedToClients: string;
}

export interface ReportsSearchQuery extends EntitySearchQuery {
reportTypes: Array<string>;
}
