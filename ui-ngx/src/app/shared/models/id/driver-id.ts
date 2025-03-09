import { EntityType } from "../entity-type.models";
import { EntityId } from "./public-api";

export class DriverId implements EntityId {
     entityType = EntityType.DRIVER;
        id: string;
        constructor(id: string){
            this.id = id;
        }
}
