import { EntityId } from './entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class ReportsId implements EntityId {
  entityType = EntityType.REPORT;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}
