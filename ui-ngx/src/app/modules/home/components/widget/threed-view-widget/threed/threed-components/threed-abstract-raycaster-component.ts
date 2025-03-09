/* eslint-disable max-len */
/* eslint-disable eqeqeq */
import { EventEmitter } from '@angular/core';
import * as THREE from 'three';
import { ROOT_TAG } from '../threed-constants';
import { IThreedSceneManager } from '../threed-managers/ithreed-scene-manager';
import { ThreedUtils } from '../threed-utils';
import { IThreedListener } from './ithreed-listener';
import { IThreedObjectSelector } from './ithreed-object-selector';
import { ThreedBaseComponent } from './threed-base-component';
import { Vector2 } from 'three';

export abstract class ThreedAbstractRaycasterComponent extends ThreedBaseComponent implements IThreedListener, IThreedObjectSelector {

    protected raycastUpdate: 'click' | 'hover';
    private resolveRaycastObject: 'single' | 'root';
    protected raycaster?: THREE.Raycaster;

    protected selectedObject: any;
    protected intersectedObjects = [];

    public raycastEnabled = true;
    public onObjectSelected: EventEmitter<any> = new EventEmitter();
    public onObjectDeselected: EventEmitter<any> = new EventEmitter();

    private hoverIndex = 0;

    constructor(raycastUpdate: 'click' | 'hover' = 'click', resolveRaycastObject: 'single' | 'root' = 'root') {
        super();

        this.raycastUpdate = raycastUpdate;
        this.resolveRaycastObject = resolveRaycastObject;
    }

    initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        this.raycaster = new THREE.Raycaster();
    }

    onKeyDown(event: KeyboardEvent): void { }
    onKeyUp(event: KeyboardEvent): void { }
    onMouseMove(event: MouseEvent): void {
        // Used to increase the performaces!
        if (this.raycastUpdate == 'hover' && this.hoverIndex++ % 2 == 0)
            {this.updateRaycaster();}
    }
    onMouseClick(event: MouseEvent): void {
        if (this.raycastUpdate == 'click')
            {this.updateRaycaster();}
    }

    protected updateRaycaster(): boolean {
        if (!this.initialized || !this.canUpdateRaycaster() || !this.raycastEnabled) {return false;}

        this.setRaycaster();
        const objs = this.raycaster.intersectObjects(this.getIntersectionObjects());
        const intersection = this.intersectedObjects = objs.filter(o => this.getIntersectedObjectFilter(o));

        console.log(intersection);

        if (intersection.length > 0) {
            const intersectedObject = intersection[0].object;
            const obj = this.resolveRaycastObject == 'root' ? ThreedUtils.findParentByChild(intersectedObject, ROOT_TAG, true) : intersectedObject;
            if (obj) {this.selectObject(obj);}
            else {this.selectObject(undefined);}
        } else {
            // if hover tooltip => nothing
            // else deselectObject
            this.selectObject(undefined);
        }

        return true;
    }

    protected setRaycaster() {
        this.raycaster.setFromCamera(this.getRaycasterOriginCoords(), this.sceneManager.camera);
    }

    private selectObject(object?: THREE.Object3D) {
        if (!object) {
            this.deselectObject();

        } else if (this.selectedObject !== object) {
            this.deselectObject();

            if (this.canSelectObject(object)) {
                this.selectedObject = object;
                this.onSelectObject(this.selectedObject);
                this.onObjectSelected.emit(this.selectedObject);
            }
        }
    }

    public deselectObject() {
        if (this.selectedObject) {
            this.onDeselectObject(this.selectedObject);
            this.onObjectDeselected.emit(this.selectedObject);
        }
        this.selectedObject = null;
    }


    protected canUpdateRaycaster(): boolean {
        return true;
    }

    protected getRaycasterOriginCoords(): THREE.Vector2 {
        return new Vector2(this.sceneManager.mouse.x, this.sceneManager.mouse.y);
    }

    protected getIntersectionObjects(): THREE.Object3D[] {
        return this.sceneManager.scene.children;
    }

    protected getIntersectedObjectFilter(o: THREE.Intersection) {
        return o.object.type !== 'TransformControlsPlane' &&
            o.object.type !== 'BoxHelper' &&
            o.object.type !== 'GridHelper' &&
            o.object.type !== 'Line' &&
            //@ts-ignore
            o.object.tag !== 'Helper' &&
            ThreedUtils.isVisible(o.object);
    }

    protected abstract onSelectObject(object: any): void;
    protected abstract canSelectObject(object: any): boolean;
    protected abstract onDeselectObject(object: any): void;

    public getSelectedObject(): THREE.Object3D {
        return this.selectedObject;
    }

}
