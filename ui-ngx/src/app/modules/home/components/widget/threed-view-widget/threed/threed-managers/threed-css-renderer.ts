import { ElementRef } from '@angular/core';
import { CSS2DRenderer } from 'three/examples/jsm/renderers/CSS2DRenderer';
import { IThreedSceneManager } from './ithreed-scene-manager';
import { IThreedRenderer } from './ithreed-renderer';

export class ThreedCssRenderer implements IThreedRenderer {
    protected cssRenderer?: CSS2DRenderer;

    constructor() {
        this.initialize();
    }

    private initialize() {
        this.initializeCssRenderer();
    }

    private initializeCssRenderer() {
        this.cssRenderer = new CSS2DRenderer();
        this.cssRenderer.domElement.style.position = 'absolute';
        this.cssRenderer.domElement.style.top = '0px';
        //this.cssRenderer.domElement.style.pointerEvents = 'none'
    }

    public attachToElement(rendererContainer: ElementRef) {
        rendererContainer.nativeElement.appendChild(this.cssRenderer.domElement);
        const rect = rendererContainer.nativeElement.getBoundingClientRect();
        this.cssRenderer.setSize(rect.width, rect.height);
    }

    public detach() {
        this.cssRenderer.domElement.remove();
    }

    public resize(width?: number, height?: number): void {
        this.cssRenderer?.setSize(width, height);
    }

    public tick(threedScene: IThreedSceneManager): void {

    }

    public render(threedScene: IThreedSceneManager): void {
        this.cssRenderer.render(threedScene.scene, threedScene.camera);
    }

    public getRenderer() {
        return this.cssRenderer;
    }
}
