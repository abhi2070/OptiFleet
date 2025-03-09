

import { Subscription } from "rxjs";
import { IThreedComponent } from "../threed-components/ithreed-component";
import { ThreedGenericSceneManager } from "../threed-managers/threed-generic-scene-manager";

export interface ThreedSceneConfig {
    shadow?: boolean,
    vr?: boolean
}
export const defaultThreedSceneConfig: ThreedSceneConfig = {
    shadow: false,
    vr: false
}

export class ThreedSceneBuilder {

    private sceneManager: ThreedGenericSceneManager;

    constructor(configs: ThreedSceneConfig = defaultThreedSceneConfig) {
        this.sceneManager = new ThreedGenericSceneManager(configs);
    }

    public add(component: IThreedComponent): ThreedSceneBuilder {
        this.sceneManager.add(component);
        return this;
    }

    public addSubscription(subscription: Subscription): ThreedSceneBuilder {
        this.sceneManager.addSubscription(subscription);
        return this;
    }

    public build(): ThreedGenericSceneManager {
        this.sceneManager.initialize();
        return this.sceneManager;
    }
}