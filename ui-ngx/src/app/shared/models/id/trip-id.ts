import { EntityType } from "../entity-type.models";
import { EntityId } from "./public-api";

export class TripId implements EntityId{
    entityType = EntityType.TRIP;
    id: string;
    constructor(id: string){
        this.id = id;
    }

}
