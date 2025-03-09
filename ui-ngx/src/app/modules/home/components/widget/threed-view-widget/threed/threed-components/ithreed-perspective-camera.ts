

import * as THREE from 'three';

export interface IThreedPerspectiveCamera {
    getPerspectiveCamera(): THREE.PerspectiveCamera;
    updateTransform(position?: THREE.Vector3, rotation?: THREE.Vector3): void;
}