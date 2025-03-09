

import { Vector3 } from "three";

export interface IThreedPerson {
    reset(position: Vector3): void;
    setDebugMode(mode: boolean): void;
}