

import { EventEmitter } from "@angular/core";
import { IThreedPhysic } from "./ithreed-physic";
import * as CANNON from 'cannon-es';
import { IThreedMesh } from "./ithreed-mesh";

export interface IThreedPhysicObject extends IThreedPhysic {
    physicBody: CANNON.Body;
    mesh?: IThreedMesh;
    tag?: string;

    onBeginCollision: EventEmitter<{ event: CANNON.Constraint, object: IThreedPhysicObject }>;
    onEndCollision: EventEmitter<{ event: CANNON.Constraint, object: IThreedPhysicObject }>;
    onCollide: EventEmitter<{ event: CANNON.Constraint }>;
}