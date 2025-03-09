import { EntityType } from "../entity-type.models";
import { EntityId } from "./public-api";

export class TripHistoryId implements EntityId{
    entityType = EntityType.TRIP_HISTORY;
    id: string;
    constructor(id: string){
        this.id = id;
    }

}