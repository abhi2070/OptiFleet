import { EntityType } from "../entity-type.models";
import { EntityId } from "./public-api";


export class VehicleId implements EntityId {
    entityType = EntityType.VEHICLE;
    id: string;
    constructor(id: string){
        this.id = id;
    }
}
