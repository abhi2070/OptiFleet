

import * as THREE from 'three';
import { OBJECT_ID_TAG, ROOT_TAG } from "../threed-constants";
import { ThreedUtils } from "../threed-utils";
import { ThreedHightlightRaycasterComponent } from "./threed-hightlight-raycaster-component";
import { CSS2DRaycaster } from '../threed-extensions/css2d-raycaster';
import { IThreedSceneManager } from '../threed-managers/ithreed-scene-manager';
import { CssObject } from '../threed-managers/threed-css-manager';

export class ThreedHightlightTooltipRaycasterComponent extends ThreedHightlightRaycasterComponent {

    private cssRaycaster: CSS2DRaycaster;

    constructor(raycastUpdate: 'click' | 'hover' = 'click', resolveRaycastObject: 'single' | 'root' = 'single', raycastOrigin?: THREE.Vector2) {
        super(raycastUpdate, resolveRaycastObject, raycastOrigin);
    }

    public initialize(sceneManager: IThreedSceneManager) {
        super.initialize(sceneManager)

        this.cssRaycaster = new CSS2DRaycaster(this.sceneManager);
    }

    protected updateRaycaster(): boolean {
        if (!super.updateRaycaster()) return false;

        const intersects = this.cssRaycaster.intersectObjects(this.sceneManager.scene.children, "a");
        // Trigger click event on <a> element
        if (intersects.length > 0) {
            intersects[0].dispatchEvent(new PointerEvent('pointerdown'));
        }
        return true;
    }

    protected onSelectObject(object: any): void {
        super.onSelectObject(object);

        this.enableTooltip(object);
    }

    protected canSelectObject(object: any): boolean {
        return !!this.getTooltip(object);
    }

    protected onDeselectObject(object: any): void {
        super.onDeselectObject(object);

        this.disableTooltip(object);
    }

    protected getTooltip(object: THREE.Group): CssObject | undefined {
        const root = ThreedUtils.findParentByChild(object, ROOT_TAG, true);
        const customId = root.userData[OBJECT_ID_TAG];

        if (customId) {
            return this.sceneManager.cssManager.findCssObject(customId);
        }
        return undefined;
    }

    protected enableTooltip(object: THREE.Group) {
        const cssObject = this.getTooltip(object);
        if (!cssObject) return;

        this.onEnableTooltip(object, cssObject);
    }

    protected onEnableTooltip(object: THREE.Group, cssObject: CssObject): void {
        const layer = cssObject.layer;
        this.getCamera().layers.enable(layer);
        this.sceneManager.cssManager.toggleMarkersLayer(false);
        object.userData.layer = layer;
    }

    protected disableTooltip(object: THREE.Group) {
        if (object) {
            this.onDisableTooltip(object)
        }
    }

    protected onDisableTooltip(object: THREE.Group) {
        const layer = object.userData.layer;
        this.sceneManager.cssManager.toggleMarkersLayer(true);

        if (layer >= this.sceneManager.cssManager.initialLabelLayerIndex)
            this.getCamera()!.layers.disable(layer);
    }

    protected getCamera(): THREE.Camera {
        return this.sceneManager.camera;
    }
}