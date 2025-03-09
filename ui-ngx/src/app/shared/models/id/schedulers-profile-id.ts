import { EntityId } from './entity-id';
import { EntityType } from '@shared/models/entity-type.models';


export class SchedulersProfileId implements EntityId{
  entityType = EntityType.SCHEDULERS_PROFILE;
  id: string;

  constructor(id?: string) {
    this.id = id;
  }

}
