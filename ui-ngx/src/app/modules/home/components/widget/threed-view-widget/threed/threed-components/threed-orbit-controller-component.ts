import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import * as TWEEN from 'three/examples/jsm/libs/tween.module.js';
import { IThreedSceneManager } from '../threed-managers/ithreed-scene-manager';
import { ThreedCssRenderer } from '../threed-managers/threed-css-renderer';
import { IThreedOrbitController } from './ithreed-orbit-controller';
import { IThreedTester } from './ithreed-tester';
import { ThreedBaseComponent } from './threed-base-component';


export class ThreedOrbitControllerComponent extends ThreedBaseComponent implements IThreedOrbitController {

    private orbit?: OrbitControls;
    private focusingOnObject = false;

    initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        this.orbit = new OrbitControls(sceneManager.camera, sceneManager.getTRenderer(ThreedCssRenderer).getRenderer().domElement);
        this.orbit.update();
    }

    tick(): void {
        this.orbit.update();
    }

    getOrbitController(): OrbitControls {
        return this.orbit;
    }

    public focusOnObject(object?: THREE.Object3D, millis: number = 0) {
        if (this.focusingOnObject) {return;}

        //this.raycastEnabledLastFrame = false;
        const selectorComponents = this.sceneManager.findComponentsByTester(IThreedTester.isIThreedObjectSelector);
        object = object || selectorComponents[0]?.getSelectedObject();

        if (millis > 0) {
            this.focusingOnObject = true;
            const duration = 300; // Duration of animation in milliseconds
            const initialPosition = this.orbit.target || new THREE.Vector3(0, 0, 0); // Start value of variable
            const finalPosition = object?.position || new THREE.Vector3(0, 0, 0); // End value of variable
            const currentPosition = new THREE.Vector3();

            new TWEEN.Tween({ value: 0 })
                .to({ value: 1 }, duration)
                .onUpdate((update: { value: number }) => {
                    currentPosition.lerpVectors(initialPosition, finalPosition, update.value);
                    this.updateOrbitTarget(currentPosition);
                })
                .onComplete(() => {
                    this.focusingOnObject = false;
                })
                .start();
        } else {
            this.updateOrbitTarget(object?.position);
        }
    }

    public zoom(distance: number = 5, millis: number = 1000) {
        const camera = this.sceneManager.camera;
        const tween = new TWEEN.Tween(camera.position)
            .to({ x: distance, y: distance, z: distance }, millis) // Zoom in to z=5 over 1 second
            .easing(TWEEN.Easing.Quadratic.Out) // Use a quadratic easing function
            .onUpdate(() => {
                this.orbit.update();
            })
            .start();
    }

    private updateOrbitTarget(position?: THREE.Vector3) {
        position = position || new THREE.Vector3(0, 0, 0);
        this.orbit.target.copy(position);
        this.orbit.update();
    }
}
