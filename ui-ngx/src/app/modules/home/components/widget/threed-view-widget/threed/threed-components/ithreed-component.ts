

import { IThreedSceneManager } from "../threed-managers/ithreed-scene-manager";

export interface IThreedComponent {
    initialize(sceneManager: IThreedSceneManager): void;
    tick(): void;
    render(): void;
    resize(): void;
    onDestroy(): void;
}