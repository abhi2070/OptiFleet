import { Camera, Scene } from 'three';
import { IThreedComponent } from '../threed-components/ithreed-component';
import { ThreedSceneConfig } from '../threed-scenes/threed-scene-builder';
import { IThreedRenderer } from './ithreed-renderer';
import { ThreedCssManager } from './threed-css-manager';
import { ThreedModelManager } from './threed-model-manager';
import { ElementRef, EventEmitter } from '@angular/core';
import { Subscription } from 'rxjs';
import { ThreedPhysicManager } from './threed-physic-manager';

export interface IThreedSceneManager {
    [x: string]: any;
    scene: Scene;
    active: boolean;
    get sceneId(): number;
    get camera(): Camera;
    get configs(): ThreedSceneConfig;
    get modelManager(): ThreedModelManager;
    get cssManager(): ThreedCssManager;
    get physicManager(): ThreedPhysicManager;
    get screenWidth(): number;
    get screenHeight(): number;
    get currentValues(): any;
    // get mouse(): THREE.Vector2;
    // get center(): THREE.Vector2;
    get vrActive(): boolean;

    onRendererContainerChange: EventEmitter<ElementRef>;
    onMainCameraChange: EventEmitter<Camera>;
    onVRChange: EventEmitter<boolean>;

    add(component: IThreedComponent, initInstantly: boolean): void;
    initialize(): void;
    getTRenderer<T extends IThreedRenderer>(type: new () => T): T | undefined;
    isActive(): boolean;
    forceUpdateValues(): void;
    getComponent<T extends IThreedComponent>(type: new () => T): T | undefined;
    findComponentsByTester<T>(tester: (obj: any) => obj is T): T[];
    destroy(): void;
    setCamera(camera: Camera): void;
    addSubscription(subscription: Subscription): void;
}
