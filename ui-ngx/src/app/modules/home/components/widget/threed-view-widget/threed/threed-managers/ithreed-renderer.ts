

import { ElementRef } from "@angular/core";
import { IThreedSceneManager } from "./ithreed-scene-manager";

export interface IThreedRenderer {
    attachToElement(rendererContainer: ElementRef): void;
    detach(): void;
    resize(width?: number, height?: number): void;
    render(threedSceneManager: IThreedSceneManager): void;
    tick(threedSceneManager: IThreedSceneManager): void;

    getRenderer(): any;
}