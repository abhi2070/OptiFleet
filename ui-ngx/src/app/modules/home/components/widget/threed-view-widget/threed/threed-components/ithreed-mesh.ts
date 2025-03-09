

import * as THREE from "three";

export interface IThreedMesh {
    // TODO: check if it is good... maybe it requires the geometry...
    getMesh(): THREE.Object3D;
}