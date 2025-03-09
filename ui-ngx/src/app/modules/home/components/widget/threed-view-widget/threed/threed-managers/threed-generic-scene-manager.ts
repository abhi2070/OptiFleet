/* eslint-disable @typescript-eslint/prefer-for-of */
import { ElementRef, EventEmitter } from '@angular/core';
import * as THREE from 'three';
import { Camera, Scene } from 'three';
import * as TWEEN from 'three/examples/jsm/libs/tween.module.js';
import { VRButton } from 'three/examples/jsm/webxr/VRButton.js';
import { IThreedComponent } from '../threed-components/ithreed-component';
import { IThreedTester } from '../threed-components/ithreed-tester';
import { ThreedSceneConfig } from '../threed-scenes/threed-scene-builder';
import { IThreedRenderer } from './ithreed-renderer';
import { IThreedSceneManager } from './ithreed-scene-manager';
import { ThreedCssManager } from './threed-css-manager';
import { ThreedCssRenderer } from './threed-css-renderer';
import { ThreedModelManager } from './threed-model-manager';
import { ThreedPhysicManager } from './threed-physic-manager';
import { ThreedWebRenderer } from './threed-web-renderer';
import { Subscription } from 'rxjs';
import { ThreedEventManager } from './threed-event-manager';
import { ThreedVrControllerComponent } from '../threed-components/threed-vr-controller-component';

export class ThreedGenericSceneManager implements IThreedSceneManager {

    private static activeSceneManagers: Map<number, boolean> = new Map();
    private static lastSceneId = 1;


    private rendererId: string;
    private rendererContainer: ElementRef;
    private threedRenderers: IThreedRenderer[] = [];
    private components: IThreedComponent[] = [];
    private subscriptions: Subscription[] = [];
    private _center = new THREE.Vector2();
    private destroying = false;

    public scene: Scene;
    public active: boolean;

    public sceneId: number;
    public camera: Camera;
    public configs: ThreedSceneConfig;
    public modelManager: ThreedModelManager;
    public cssManager: ThreedCssManager;
    public physicManager: ThreedPhysicManager;
    public screenWidth = window.innerWidth;
    public screenHeight = window.innerHeight;
    public currentValues: any;
    public mouse = new THREE.Vector2();
    public vrActive = false;

    public get center(): THREE.Vector2 {
        const rect: DOMRect | undefined = this.rendererContainer?.nativeElement.getBoundingClientRect();
        if (rect) {
            this._center.x = rect.x + (rect.right - rect.left) / 2;
            this._center.y = rect.y + (rect.bottom - rect.top) / 2;
        }
        return this._center;
    }


    public onRendererContainerChange = new EventEmitter<ElementRef>();
    public onMainCameraChange = new EventEmitter<Camera>();
    public onVRChange = new EventEmitter<boolean>();
    public onTick = new EventEmitter();
    public onRender = new EventEmitter();

    constructor(configs: ThreedSceneConfig) {
        this.configs = configs;
        this.active = true;

        this.sceneId = ThreedGenericSceneManager.lastSceneId++;
        ThreedGenericSceneManager.activeSceneManagers.set(this.sceneId, false);
    }

    public initialize() {
        this.threedRenderers.push(new ThreedWebRenderer());
        this.threedRenderers.push(new ThreedCssRenderer());
        this.initializeEventListeners();
        this.initializeVR();
        this.scene = new THREE.Scene();

        this.modelManager = new ThreedModelManager(this);
        const s = this.modelManager.onAfterAddModel.subscribe(_ => this.updateValues());
        this.subscriptions.push(s);

        this.cssManager = new ThreedCssManager(this);
        this.physicManager = new ThreedPhysicManager(this);
        this.components.forEach(c => c.initialize(this));
    }

    public add(component: IThreedComponent, initInstantly: boolean = false): void {
        this.components.push(component);
        if (initInstantly) {component.initialize(this);}
    }

    public addSubscription(subscription: Subscription): void {
        this.subscriptions.push(subscription);
    }

    public getTRenderer<T extends IThreedRenderer>(type: new () => T): T | undefined {
        return this.threedRenderers.find(c => c instanceof type) as T | undefined;
    }

    public setCamera(camera: Camera) {
        this.camera = camera;
        this.onMainCameraChange.emit(camera);
    }

    public attachToElement(rendererContainer: ElementRef) {
        if (this.rendererContainer)
            {throw new Error('Render container already defined!');}

        this.threedRenderers.forEach(r => r.attachToElement(rendererContainer));
        this.rendererContainer = rendererContainer;
        this.rendererId = `RendererContainerID_${this.sceneId}`;
        this.rendererContainer.nativeElement.setAttribute('id', this.rendererId);
        this.onRendererContainerChange.emit(rendererContainer);

        if (this.configs.vr) {
            const vrButton = VRButton.createButton(this.getTRenderer(ThreedWebRenderer).getRenderer());
            vrButton.addEventListener('click', event => { event.stopPropagation(); });
            this.rendererContainer.nativeElement.appendChild(vrButton);
        }

        this.startRendering();
    }

    public detach() {

    }

    public resize(width?: number, height?: number): void {
        const rect: DOMRect | undefined = this.rendererContainer?.nativeElement.getBoundingClientRect();
        this.screenWidth = width || rect.width;
        this.screenHeight = height || rect.height;

        this.threedRenderers.forEach(r => r.resize(this.screenWidth, this.screenHeight));
        this.components.forEach(c => c.resize());
    }

    public isActive(): boolean {
        return this.active && ThreedGenericSceneManager.activeSceneManagers.get(this.sceneId) === true;
    }

    public getComponent<T extends IThreedComponent>(type: new (...args: any[]) => T): T | undefined {
        return this.components.find(c => c instanceof type) as T | undefined;
    }

    public getComponents<T extends IThreedComponent>(type: new (...args: any[]) => T): T[] {
        return this.components.filter(c => c instanceof type) as T[];
    }

    public findComponentsByTester<T>(tester: (obj: any) => obj is T): T[] {
        const components: T[] = [];
        for (let index = 0; index < this.components.length; index++) {
            const component = this.components[index];
            if (tester(component))
                {components.push(component);}
        }
        return components;
    }

    /**========================================================================
     *                           UPDATE VALUES
     *========================================================================**/
    public setValues(values: any) {
        this.currentValues = { ...this.currentValues, ...values };

        console.log(this.currentValues);

        this.updateValues();
    }

    public forceUpdateValues() {
        this.updateValues();
    }

    private updateValues() {
        if (!this.currentValues) {return;}

        const updatables = this.findComponentsByTester(IThreedTester.isIThreedUpdatable);
        updatables.forEach(c => c.onUpdateValues(this.currentValues));
        this.cssManager.onUpdateValues();

        this.render();
    }
    /*============================ END OF UPDATE VALUES ============================*/



    /**========================================================================
     *                           UPDATE & RENDERING
     *========================================================================**/
    private startRendering() {
        this.resize();

        if (this.configs.vr) {
            this.getTRenderer(ThreedWebRenderer).getRenderer().setAnimationLoop(() => this.loop());
        } else {this.animate();}
    }

    private animate() {
        if(this.destroying) {return;}

        window.requestAnimationFrame(() => this.animate());

        this.loop();
    }

    private loop() {
        this.tick();
        this.physicManager.updatePhysics();
        this.physicManager.updateVisuals();
        TWEEN.update();
        this.render();
    }

    private tick(): void {
        this.cssManager.tick();
        this.components.forEach(c => c.tick());
        this.threedRenderers.forEach(r => r.tick(this));
        this.onTick.emit();
    }

    private render(): void {
        this.threedRenderers.forEach(r => r.render(this));
        this.components.forEach(c => c.render());
        this.onRender.emit();
    }
    /*============================ END OF UNDATE & RENDERING ============================*/


    /**========================================================================
     *                           EVENTS
     *========================================================================**/
    private initializeEventListeners() {
        this.subscriptions.push(
            ThreedEventManager.instance.onMouseMove.subscribe(event => this.mouseMove(event)),
            ThreedEventManager.instance.onMouseClick.subscribe(event => this.mouseClick(event)),
            ThreedEventManager.instance.onKeyDown.subscribe(event => this.keyDown(event)),
            ThreedEventManager.instance.onKeyUp.subscribe(event => this.keyUp(event))
        );
    }

    private mouseMove(event: MouseEvent) {
        this.calculateMousePosition(event);

        if (!this.isActive()) {return;}

        const listeners = this.findComponentsByTester(IThreedTester.isIThreedListener);
        listeners.forEach(c => c.onMouseMove(event));
    }
    private mouseClick(event: MouseEvent) {
        this.calculateMousePosition(event);

        if (!this.isActive()) {return;}

        const listeners = this.findComponentsByTester(IThreedTester.isIThreedListener);
        listeners.forEach(c => c.onMouseClick(event));
    }
    private keyDown(event: KeyboardEvent) {
        if (!this.isActive()) {return;}

        const listeners = this.findComponentsByTester(IThreedTester.isIThreedListener);
        listeners.forEach(c => c.onKeyDown(event));
    }
    private keyUp(event: KeyboardEvent) {
        if (!this.isActive()) {return;}

        const listeners = this.findComponentsByTester(IThreedTester.isIThreedListener);
        listeners.forEach(c => c.onKeyUp(event));
    }

    private calculateMousePosition(event: MouseEvent) {
        if (!this.rendererContainer || !this.active) {return;}

        const rect = this.rendererContainer.nativeElement.getBoundingClientRect();

        this.mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
        this.mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

        if (this.mouse.x < 1 && this.mouse.x > -1 && this.mouse.y < 1 && this.mouse.y > -1) {

            /*
            const elementsAbove = Array.from(document.querySelectorAll(`:not(#${this.rendererId}) *`))
                .filter(el => {
                    const { zIndex, display } = getComputedStyle(el);
                    if (display !== 'none' && zIndex !== 'auto' && parseInt(zIndex) > 50) {
                        const rect2 = el.getBoundingClientRect();
                        return !(rect2.right < rect.left ||
                            rect2.left > rect.right ||
                            rect2.bottom < rect.top ||
                            rect2.top > rect.bottom);
                    }
                    return false;
                });

            const active = elementsAbove.length == 0;
            ThreedGenericSceneManager.activeSceneManagers.set(this.sceneId, active);
            if (active) event.preventDefault();*/

            ThreedGenericSceneManager.activeSceneManagers.set(this.sceneId, true);
            event.preventDefault();
        } else {ThreedGenericSceneManager.activeSceneManagers.set(this.sceneId, false);}
    }
    /*============================ END OF EVENTS ============================*/


    /**========================================================================
     *                           VR SECTION
     *========================================================================**/

    private initializeVR() {
        if (this.configs.vr) {
            const renderer = this.getTRenderer(ThreedWebRenderer).getRenderer();
            renderer.xr.enabled = true;
            renderer.xr.addEventListener('sessionstart', () => this.onVRSessionStart());
            renderer.xr.addEventListener('sessionend', () => this.onVRSessionEnd());
        }
    }

    private cameraGroup: THREE.Group;
    private onVRSessionStart() {
        if (this.camera && this.camera.parent?.type !== 'Group') {
            this.cameraGroup = new THREE.Group();
            this.cameraGroup.name = 'VR Camera Group';
            this.cameraGroup.add(this.camera);
            this.cameraGroup.position.copy(this.camera.position);
            this.cameraGroup.position.setY(0);
            //this.cameraGroup.rotation.copy(this.camera.rotation);
            this.camera.position.set(0, 0, 0);
            this.camera.rotation.set(0, 0, 0);
            this.scene.add(this.cameraGroup);
        }
        this.vrActive = true;
        this.onVRChange.emit(true);
    }
    private onVRSessionEnd() {
        if (this.camera && this.camera.parent?.type === 'Group') {
            const position = new THREE.Vector3();
            const rotation = new THREE.Euler();
            position.copy(this.cameraGroup.position);
            rotation.copy(this.camera.rotation);
            this.scene.remove(this.cameraGroup);
            this.camera.parent = null;
            this.camera.position.copy(position);
            this.camera.position.setY(1.7);
            this.camera.rotation.set(0, rotation.y, 0);
        }
        this.vrActive = false;
        this.onVRChange.emit(false);
    }
    /*============================ END OF VR ============================*/


    public destroy(): void {
        this.destroying = true;
        if (this.configs.vr)
            {this.getTRenderer(ThreedWebRenderer).getRenderer().setAnimationLoop(null);}

        this.threedRenderers.forEach(r => r.detach());
        this.components.forEach(c => c.onDestroy());
        this.subscriptions.forEach(s => s.unsubscribe());
        this.cssManager.onDestroy();
        this.modelManager.onDestroy();
        ThreedGenericSceneManager.activeSceneManagers.delete(this.sceneId);
    }
}
