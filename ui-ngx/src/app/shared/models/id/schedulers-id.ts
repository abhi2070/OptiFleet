import { EntityId } from './entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class SchedulersId implements EntityId {
  entityType = EntityType.SCHEDULERS;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}
