

import { BaseData } from '@shared/models/base-data';
import { TenantId } from '@shared/models/id/tenant-id';
import { EntityId } from '@shared/models/id/entity-id';
import { EventId } from './id/event-id';
import { ContentType } from '@shared/models/constants';
import { EntityType } from '@shared/models/entity-type.models';

export enum EventType {
  ERROR = 'ERROR',
  LC_EVENT = 'LC_EVENT',
  STATS = 'STATS',
  DEBUG_INTEGRATION='DEBUG_INTEGRATION'
}

export enum DebugEventType {
  DEBUG_RULE_NODE = 'DEBUG_RULE_NODE',
  DEBUG_RULE_CHAIN = 'DEBUG_RULE_CHAIN'
}

export const eventTypeTranslations = new Map<EventType | DebugEventType, string>(
  [
    [EventType.ERROR, 'event.type-error'],
    [EventType.LC_EVENT, 'event.type-lc-event'],
    [EventType.STATS, 'event.type-stats'],
    [EventType.DEBUG_INTEGRATION, 'event.type-integration'],
    [DebugEventType.DEBUG_RULE_NODE, 'event.type-debug-rule-node'],
    [DebugEventType.DEBUG_RULE_CHAIN, 'event.type-debug-rule-chain'],
  ]
);

export interface BaseEventBody {
  server: string;
}

export interface ErrorEventBody extends BaseEventBody {
  method: string;
  error: string;
}

export interface LcEventEventBody extends BaseEventBody {
  event: string;
  success: boolean;
  error: string;
}

export interface StatsEventBody extends BaseEventBody {
  messagesProcessed: number;
  errorsOccurred: number;
}

export interface DebugRuleNodeEventBody extends BaseEventBody {
  type: string;
  entityId: string;
  entityType: string;
  msgId: string;
  msgType: string;
  relationType: string;
  dataType: ContentType;
  data: string;
  metadata: string;
  error: string;
}

export interface DebugRuleChainEventBody extends BaseEventBody {
  message: string;
  error?: string;
}

export type EventBody = ErrorEventBody & LcEventEventBody & StatsEventBody & DebugRuleNodeEventBody & DebugRuleChainEventBody;

export interface Event extends BaseData<EventId> {
  tenantId: TenantId;
  entityId: EntityId;
  type: string;
  uid: string;
  body: EventBody;
}

export interface BaseFilterEventBody {
  server?: string;
}

export interface ErrorFilterEventBody extends BaseFilterEventBody {
  method?: string;
  errorStr?: string;
}

export interface LcFilterEventEventBody extends BaseFilterEventBody {
  event?: string;
  status?: string;
  errorStr?: string;
}

export interface StatsFilterEventBody extends BaseFilterEventBody {
  messagesProcessed?: number;
  errorsOccurred?: number;
}

export interface DebugFilterRuleNodeEventBody extends BaseFilterEventBody {
  msgDirectionType?: string;
  entityId?: string;
  entityName?: EntityType;
  msgId?: string;
  msgType?: string;
  relationType?: string;
  dataSearch?: string;
  metadataSearch?: string;
  isError?: boolean;
  errorStr?: string;
}

export type FilterEventBody = ErrorFilterEventBody & LcFilterEventEventBody & StatsFilterEventBody & DebugFilterRuleNodeEventBody;
