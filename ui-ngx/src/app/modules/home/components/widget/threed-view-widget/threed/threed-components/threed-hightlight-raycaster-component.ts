/* eslint-disable max-len */
import * as THREE from 'three';
import { IThreedSceneManager } from '../threed-managers/ithreed-scene-manager';
import { ThreedUtils } from '../threed-utils';
import { ThreedAbstractRaycasterComponent } from './threed-abstract-raycaster-component';

export class ThreedHightlightRaycasterComponent extends ThreedAbstractRaycasterComponent {

    private hoveringColor: {
        color: THREE.Color;
        alpha: number;
    };
    private hoveringMaterial: THREE.MeshStandardMaterial;
    private raycastOrigin?: THREE.Vector2;

    constructor(raycastUpdate: 'click' | 'hover' = 'click', resolveRaycastObject: 'single' | 'root' = 'single', raycastOrigin?: THREE.Vector2) {
        super(raycastUpdate, resolveRaycastObject);

        this.raycastOrigin = raycastOrigin;
    }

    initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        this.setHoveringColor();
    }

    public setHoveringColor(hoveringColor: string = 'rgba(0,0,255,0.5)', wireframe = false) {
        if(!hoveringColor) {return;}

        this.hoveringColor = ThreedUtils.getAlphaAndColorFromString(hoveringColor);
        this.hoveringMaterial = new THREE.MeshStandardMaterial({
            color: this.hoveringColor.color,
            opacity: this.hoveringColor.alpha,
            transparent: true,
            wireframe,
        });
    }

    protected getRaycasterOriginCoords(): THREE.Vector2 {
        return this.raycastOrigin || super.getRaycasterOriginCoords();
    }

    protected onSelectObject(object: any): void {
        this.toggleHightlightGLTF(object, true);
    }

    protected canSelectObject(object: any): boolean {
        return true;
    }

    protected onDeselectObject(object: any): void {
        this.toggleHightlightGLTF(object, false);
    }

    protected toggleHightlightGLTF(root: THREE.Group | THREE.Object3D, enable: boolean, material?: THREE.MeshStandardMaterial) {
        root.traverse(o => {
            if (o instanceof THREE.Mesh) {
                if (enable) {
                    o.userData.currentMaterial = o.material;
                    o.material = material ?? this.hoveringMaterial;
                } else {
                    o.material = o.userData.currentMaterial;
                }
            }
        });
    }
}
