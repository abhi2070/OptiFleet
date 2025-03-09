

import * as THREE from 'three';

export interface IThreedObjectSelector {
    getSelectedObject(): THREE.Object3D | undefined;
    deselectObject(): void;
}