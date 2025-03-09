

import * as THREE from 'three';
import { IThreedSceneManager } from "../threed-managers/ithreed-scene-manager";
import { IThreedPerspectiveCamera } from "./ithreed-perspective-camera";
import { ThreedBaseComponent } from "./threed-base-component";
import { GIZMOS_LAYER } from '../threed-constants';

export class ThreedPerspectiveCameraComponent extends ThreedBaseComponent implements IThreedPerspectiveCamera {

    private camera: THREE.PerspectiveCamera;
    private initialPosition: THREE.Vector3 = new THREE.Vector3(0, 40, -70);

    constructor(initialPosition?: THREE.Vector3){
        super();
        this.initialPosition = initialPosition ?? this.initialPosition;
    }

    initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        this.camera = new THREE.PerspectiveCamera(60, this.sceneManager.screenWidth / this.sceneManager.screenHeight, 0.01, 10000);
        this.camera.position.copy(this.initialPosition);
        this.camera.layers.enable(GIZMOS_LAYER);
        sceneManager.setCamera(this.camera);
    }

    resize(): void {
        this.camera!.aspect = this.sceneManager.screenWidth / this.sceneManager.screenHeight;
        this.camera!.updateProjectionMatrix();
    }

    getPerspectiveCamera(): THREE.PerspectiveCamera {
        return this.camera;
    }

    updateTransform(position?: THREE.Vector3, rotation?: THREE.Vector3): void {
        if(!this.camera) return;
        if(position) this.camera.position.set(position.x, position.y, position.z);
        if(rotation) this.camera.rotation.set(rotation.x, rotation.y, rotation.z);
    }
}