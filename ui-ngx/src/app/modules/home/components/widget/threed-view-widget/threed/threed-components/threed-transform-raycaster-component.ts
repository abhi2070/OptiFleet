/* eslint-disable max-len */
import { IThreedSceneManager } from '../threed-managers/ithreed-scene-manager';
import { ThreedAbstractRaycasterComponent } from './threed-abstract-raycaster-component';
import { ThreedTransformControllerComponent } from './threed-transform-controller-component';
import * as THREE from 'three'

export class ThreedTransformRaycasterComponent extends ThreedAbstractRaycasterComponent {

    private transformController: ThreedTransformControllerComponent;
    private updateRaycast = true;
    private updateRaycastLastFrame = true;

    constructor(raycastUpdate: 'click' | 'hover' = 'click', resolveRaycastObject: 'single' | 'root' = 'root', transformController: ThreedTransformControllerComponent) {
        super(raycastUpdate, resolveRaycastObject);
        this.transformController = transformController;
    }

    initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        const s = this.transformController.onDraggingChanged.subscribe((event: THREE.Event & { type: 'dragging-changed'; value: boolean }) => {
            this.updateRaycast = !event.value;
            if (event.value) { this.updateRaycastLastFrame = false; }
        });
        this.subscriptions.push(s);
    }

    protected canUpdateRaycaster(): boolean {
        if (!this.updateRaycast) {return false;}
        if (!this.updateRaycastLastFrame) {
            this.updateRaycastLastFrame = true;
            return false;
        }
        return true;
    }

    protected onSelectObject(object: any): void {
        this.transformController.attachTransformController(object);
    }

    protected canSelectObject(object: any): boolean {
        return true;
    }

    protected onDeselectObject(object: any): void {
        this.transformController.attachTransformController(undefined);
    }
}
