

import * as THREE from 'three';
import { CAMERA_ID } from "../../threed-constants";
import { ThreedCameraSettings } from "../../threed-models";
import { IThreedPerspectiveCamera } from "../ithreed-perspective-camera";
import { IThreedUpdatable } from "../ithreed-updatable";
import { ThreedBaseComponent } from "../threed-base-component";


export class ThreedUpdateCameraComponent extends ThreedBaseComponent implements IThreedUpdatable {

    private threedCamera?: IThreedPerspectiveCamera;

    constructor(threedCamera?: IThreedPerspectiveCamera) {
        super();
        this.threedCamera = threedCamera;
    }

    onUpdateValues(values: any): void {
        if (!values) return;
        const settings: ThreedCameraSettings = values;
        if (!settings) return;

        this.sceneManager.modelManager.updateModelTransforms(CAMERA_ID, { threedPositionVectorSettings: settings.initialPosition, threedRotationVectorSettings: settings.initialRotation });

        console.log("Update values", this.threedCamera);
        if (this.threedCamera) {
            const camera = this.threedCamera.getPerspectiveCamera();

            const threedPosition = settings.initialPosition;
            const threedRotation = settings.initialRotation;
            let position: THREE.Vector3 | undefined;
            let rotation: THREE.Vector3 | undefined;
            if (threedPosition) {
                position = new THREE.Vector3(threedPosition.x, threedPosition.y, threedPosition.z);
            }
            if (threedRotation) {
                rotation = new THREE.Vector3(THREE.MathUtils.degToRad(threedRotation.x), THREE.MathUtils.degToRad(threedRotation.y), THREE.MathUtils.degToRad(threedRotation.z));
            }
            this.threedCamera.updateTransform(position, rotation);

            camera.far = settings.far || camera.far;
            camera.near = settings.near || camera.near;
            camera.fov = settings.fov || camera.fov;
            camera.updateProjectionMatrix();
        }
    }
}