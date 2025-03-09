/* eslint-disable max-len */
/* eslint-disable @typescript-eslint/prefer-for-of */
/* eslint-disable eqeqeq */
/* eslint-disable @typescript-eslint/no-non-null-assertion */
import { CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer';
import { LAST_VISIBILITY, OBJECT_ID_TAG } from '../threed-constants';
import { IThreedSceneManager } from './ithreed-scene-manager';
import * as THREE from 'three';
import { Subscription } from 'rxjs';
import { VrUi } from '../threed-extensions/vr-ui';
import { ThreedWebRenderer } from './threed-web-renderer';
import { createText } from 'three/examples/jsm/webxr/Text2D';
import * as TWEEN from 'three/examples/jsm/libs/tween.module.js';

export type CssObjectType = 'label' | 'image';

export interface CssData {
    id: number;
    htmlElement: HTMLElement;
    cssObject: CSS2DObject;
    type: CssObjectType;
    offsetY: number; //ranges between 0...1;

    vrMesh?: THREE.Group;
}

export interface CssObject {
    layer: number;
    data: CssData[];
}

export interface CssObjectProperties {
    type: CssObjectType;
    className?: string;
    offsetY: number; //ranges between 0...1;
    alwaysVisible?: boolean;
}

export class ThreedCssManager {

    private static lastCssObjectId = 1;

    public cssObjects: Map<string, CssObject> = new Map();
    public vrUiObjects: ({ mesh: THREE.Group | THREE.Object3D; offset: THREE.Vector3; timeout: NodeJS.Timeout | undefined })[] = [];
    private sceneManager: IThreedSceneManager;

    public readonly markersLayerIndex = 4;
    public readonly initialLabelLayerIndex = 5;
    private lastLayerIndex = this.initialLabelLayerIndex;
    private subscriptions: Subscription[] = [];

    private markersLayerEnabled = true;

    constructor(sceneManager: IThreedSceneManager) {
        this.sceneManager = sceneManager;

        const s = this.sceneManager.onMainCameraChange.subscribe(c => this.updateMarkersLayer());
        const s2 = this.sceneManager.onVRChange.subscribe(v => {
            this.updateObjectVisibility();
        });
        this.subscriptions.push(s);
        this.subscriptions.push(s2);
    }

    public toggleMarkersLayer(enabled?: boolean): void {
        this.markersLayerEnabled = enabled || !this.markersLayerEnabled;
        this.updateMarkersLayer();
    }

    private updateMarkersLayer(): void {
        if (!this.sceneManager.camera) {return;}

        if (this.markersLayerEnabled) {this.sceneManager.camera.layers.enable(this.markersLayerIndex);}
        else {this.sceneManager.camera.layers.disable(this.markersLayerIndex);}
    }

    public createObject(id: string, properties: CssObjectProperties): CssData {
        let htmlElement: HTMLElement;
        switch (properties.type) {
            case 'label':
                htmlElement = this.createLabel(properties.className);
                break;
            case 'image':
                htmlElement = this.createImage(properties.className);
                break;
        }

        if (!this.cssObjects.has(id))
            {this.cssObjects.set(id, { layer: this.lastLayerIndex++, data: [] });}

        const cssObject = this.cssObjects.get(id);

        const css2dObject = new CSS2DObject(htmlElement);
        css2dObject.layers.set(properties.alwaysVisible ? this.markersLayerIndex : cssObject.layer);
        css2dObject.userData[OBJECT_ID_TAG] = id;

        const offsetY = THREE.MathUtils.clamp(properties.offsetY, 0, 1);
        const model = this.sceneManager.modelManager.models.get(id);
        if (model) {
            // it places the label to the center of the model if it exists
            const position = new THREE.Vector3();
            const box = new THREE.Box3().setFromObject(model.root);
            box.getCenter(position);
            const size = new THREE.Vector3();
            box.getSize(size);

            position.y += (offsetY - 0.5) * size.y;

            css2dObject.position.copy(position);
        }

        const objectId = ThreedCssManager.lastCssObjectId++;
        const cssData: CssData = { htmlElement, cssObject: css2dObject, type: properties.type, id: objectId, offsetY };

        cssObject.data.push(cssData);

        this.sceneManager.scene.add(cssData.cssObject);

        return cssData;
    }

    onUpdateValues() {
        // recalculate the position of the css object according to the positions of the models!
        this.cssObjects.forEach((v, k) => {
            const model = this.sceneManager.modelManager.models.get(k);
            if (model) {
                // it places the label to the center of the model if it exists
                const position = new THREE.Vector3();
                const box = new THREE.Box3().setFromObject(model.root);
                box.getCenter(position);
                const size = new THREE.Vector3();
                box.getSize(size);

                const distance = Math.max(size.x, size.z) / 2 + 1;
                v.data.forEach(d => {
                    const p = new THREE.Vector3().copy(position);
                    p.y += (d.offsetY - 0.5) * size.y;
                    d.cssObject.position.copy(p);

                    if (d.vrMesh) {
                        d.vrMesh.children[0].position.set(0, 0, distance);
                    }
                });
            }
        });
    }

    public tick() {
        if (this.sceneManager.vrActive) {
            const xr = this.sceneManager.getTRenderer(ThreedWebRenderer).getRenderer().xr;
            const cameraPosition = xr.getCamera().position;

            this.cssObjects.forEach((v, k) => {
                v.data.forEach(e => {
                    if (e.vrMesh) {
                        e.vrMesh.lookAt(cameraPosition);
                    }
                });
            });

            /*this.vrUiObjects.forEach(o => {
                o.mesh.position.set(cameraPosition.x + o.offset.x, cameraPosition.y + o.offset.y, cameraPosition.z + o.offset.z);
                o.mesh.lookAt(cameraPosition);
            })*/
        }
    }

    private createLabel(className?: string): HTMLDivElement {
        const divElement = document.createElement('div');
        divElement.className = className || 'label';
        divElement.textContent = 'initial content';
        divElement.style.marginTop = '-1em';
        return divElement;
    }

    private createVRLabel(cssDataAndLayer: {
        data: CssData;
        layer: number;
        id: string;
    }, content: string) {
        const cssData = cssDataAndLayer.data;
        const wasVisible = cssData.vrMesh?.visible ?? false;

        if (cssData.vrMesh) {this.sceneManager.scene.remove(cssData.vrMesh);}
        //cssData.vrMesh?.remove();
        const panel = VrUi.createPanelFromHtml(content, { textSize: .08, margin: .5 });
        panel.position.copy(cssData.cssObject.position);
        panel.layers.set(cssDataAndLayer.layer);
        panel.renderOrder = 10;
        const model = this.sceneManager.modelManager.models.get(cssDataAndLayer.id);
        if (model) {
            const box = new THREE.Box3().setFromObject(model.root);
            const center = new THREE.Vector3();
            const size = new THREE.Vector3();
            box.getCenter(center);
            box.getSize(size);

            const distance = Math.max(size.x, size.z) / 2 + 1;
            panel.position.set(0, 0, distance);
        }
        const pivot = new THREE.Group();
        pivot.add(panel);
        pivot.position.copy(cssData.cssObject.position);
        pivot.visible = wasVisible;
        cssData.vrMesh = pivot;
        this.sceneManager.scene.add(pivot);
    }

    private createImage(className?: string): HTMLImageElement {
        const imgElement = document.createElement('img');
        imgElement.className = className || '';
        imgElement.style.marginTop = '-1em';
        return imgElement;
    }

    private createVRImage(cssDataAndLayer: {
        data: CssData;
        layer: number;
        id: string;
    }, content: { url: string; size: number }) {
        const cssData = cssDataAndLayer.data;
        const wasVisible = cssData.vrMesh?.visible ?? this.sceneManager.vrActive;

        if (cssData.vrMesh) {this.sceneManager.scene.remove(cssData.vrMesh);}
        const panel = VrUi.createImg(content.url, content.size / 200, content.size / 200);
        panel.position.copy(cssData.cssObject.position);
        panel.visible = wasVisible;
        //@ts-ignore
        cssData.vrMesh = panel;
        this.sceneManager.scene.add(panel);
    }

    /*
    public removeLabel(id: string): void {
        this.cssObjects.delete(id);
    }*/

    /**
     * It updates the first label finded with the specific id.
     * Example: if we call updateLabelContent(['id1', 'id2'], "some html"),
     * it will check first for a label with id 'id1' and if it does not exist,
     * it tries to find a label with id 'id2'. If the label exists,
     * it will be updated otherwise nothing happens.
     *
     * @param ids the list of id to search
     * @param content the new content
     * @returns
     */
    public updateLabel(ids: string[], content: string): CssData | undefined {
        console.log('this.updateLabel', ids, content);

        const cssDataAndLayer = this.findFirstElement(ids, 'label');
        const cssData = cssDataAndLayer?.data;
        if (!cssData) {return;}

        this.createVRLabel(cssDataAndLayer, content);

        const divLabel = cssData!.htmlElement;
        divLabel.innerHTML = content;
        return cssData;
    }

    public updateImage(ids: string[], content: { url: string; size: number }): CssData | undefined {
        const cssDataAndLayer = this.findFirstElement(ids, 'image');
        const cssData = cssDataAndLayer?.data;
        if (!cssData) {return;}

        this.createVRImage(cssDataAndLayer, content);

        const image = cssData!.htmlElement as HTMLImageElement;
        image.src = content.url;
        image.width = content.size || 34;
        image.height = content.size || 34;
        /*
        image.style.backgroundImage = `url(${content.url})`;
        image.style.width = `width: ${content.size || 34}px;`;
        image.style.height = `height: ${content.size || 34}px;`;
        */
        return cssData;
    }

    private updateObjectVisibility() {
        const vrActive = this.sceneManager.vrActive;
        this.cssObjects.forEach((v: CssObject, k: string) => {
            v.data.forEach(e => {
                if (e.vrMesh) {
                    e.vrMesh.visible = vrActive ? e.vrMesh.userData[LAST_VISIBILITY] || (e.type == 'image') : false;
                }
                e.cssObject.visible = !vrActive;
            });
        });

        this.vrUiObjects.forEach(o => {
            o.mesh.parent = vrActive ? this.sceneManager.camera : null;
            o.mesh.visible = vrActive;
        });
    }

    private findFirst(ids: string[], type: CssObjectType): { mapId: string; arrayId: number; layer: number; id: string } | undefined {
        for (let i = 0; i < ids.length; i++) {
            const id = ids[i];
            const cssObject = this.cssObjects.get(id);
            if (!cssObject) {continue;}
            else {
                for (let j = 0; j < cssObject.data.length; j++) {
                    const element = cssObject.data[j];
                    if (element.type == type) {
                        return { mapId: ids[i], arrayId: j, layer: cssObject.layer, id };
                    }
                }
            }
        }
        return undefined;
    }

    public findFirstElement(ids: string[], type: CssObjectType): { data: CssData; layer: number; id: string } | undefined {
        const id = this.findFirst(ids, type);
        if (!id) {return;}

        return { data: this.cssObjects.get(id.mapId).data[id.arrayId], layer: id.layer, id: id.id };
    }

    public findCssObject(...ids: string[]): CssObject | undefined {
        for (let i = 0; i < ids.length; i++) {
            const id = ids[i];
            const cssObject = this.cssObjects.get(id);
            if (cssObject)
                {return cssObject;}
        }
        return undefined;
    }

    public onDestroy() {
        this.subscriptions.forEach(s => s.unsubscribe());
    }

    public createVRText(text: string, offset: THREE.Vector3, panel: boolean = false, color: string = '#ffffff', height: number = .25, destroyAfterSeconds?: number): { mesh: THREE.Object3D | THREE.Group; offset: THREE.Vector3 } {
        const mesh = panel ? VrUi.createPanelFromHtml(text, { textSize: height, margin: .5 }) : VrUi.createText(text, height, color);
        let timeout: NodeJS.Timeout | undefined;
        if (destroyAfterSeconds) {
            timeout = setTimeout(() => {
                const index = this.vrUiObjects.findIndex(o => o.mesh.uuid == mesh.uuid);
                //console.log("remove mesh...", index, mesh, this.vrUiObjects);
                if (index != -1) {
                    if (mesh.parent) {mesh.parent.remove(mesh);}
                    this.sceneManager.scene.remove(mesh);
                    this.vrUiObjects.splice(index, 1);
                }
            }, destroyAfterSeconds * 1000);
        }

        mesh.visible = this.sceneManager.vrActive;
        mesh.position.copy(offset);
        this.sceneManager.scene.add(mesh);
        mesh.parent = this.sceneManager.vrActive ? this.sceneManager.camera : null;

        const obj = { mesh, offset, timeout };
        this.vrUiObjects.push(obj);
        return obj;
    }

    public createOrUpdateVRText(text: string, offset: THREE.Vector3, panel: boolean = false, color: string = '#ffffff', height: number = .25, uuid?: string, destroyAfterSeconds?: number): { mesh: THREE.Object3D | THREE.Group; offset: THREE.Vector3 } {
        const index = this.vrUiObjects.findIndex(o => o.mesh.uuid == uuid);
        if (index != -1) {
            const mesh = this.vrUiObjects[index].mesh;
            const timeout = this.vrUiObjects[index].timeout;

            if (timeout) {clearTimeout(timeout);}
            if (mesh.parent) {mesh.parent.remove(mesh);}
            this.sceneManager.scene.remove(mesh);
            this.vrUiObjects.splice(index, 1);
        }
        const obj = this.createVRText(text, offset, panel, color, height, destroyAfterSeconds);
        if (uuid) {obj.mesh.uuid = uuid;}
        return obj;
    }
}
