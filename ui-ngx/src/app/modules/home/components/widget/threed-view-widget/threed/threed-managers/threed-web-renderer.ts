

import { ElementRef } from "@angular/core";
import { IThreedSceneManager } from "./ithreed-scene-manager";
import { IThreedRenderer } from "./ithreed-renderer";
import * as THREE from 'three';

export class ThreedWebRenderer implements IThreedRenderer {
    protected renderer?: THREE.WebGLRenderer;

    constructor() {
        this.initialize();
    }

    private initialize() {
        this.initializeRenderer();
    }

    private initializeRenderer() {
        this.renderer = new THREE.WebGLRenderer();
        this.renderer.autoClear = false;
    }

    public attachToElement(rendererContainer: ElementRef) {
        const canvas = this.renderer.domElement;
        canvas.style.width = '100%';
        canvas.style.height = '100%';
        rendererContainer.nativeElement.appendChild(canvas);
        this.renderer.setPixelRatio(window.devicePixelRatio);
    }

    public detach(): void {
        this.renderer.domElement.remove();
    }

    public resize(width?: number, height?: number): void {
        this.renderer?.setSize(width, height, false);
    }

    public tick(threedSceneManager: IThreedSceneManager): void { 
        
    }

    public render(threedSceneManager: IThreedSceneManager): void {
        this.renderer.clear();
        this.renderer.render(threedSceneManager.scene, threedSceneManager.camera);
    }

    public getRenderer() {
        return this.renderer;
    }
}