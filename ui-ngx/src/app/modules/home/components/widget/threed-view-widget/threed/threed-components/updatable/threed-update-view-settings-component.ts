

import { ThreedComplexOrbitWidgetSettings } from "../../threed-models";
import { IThreedSceneManager } from "../../threed-managers/ithreed-scene-manager";
import { IThreedPerspectiveCamera } from "../ithreed-perspective-camera";
import { IThreedUpdatable } from "../ithreed-updatable";
import { ThreedBaseComponent } from "../threed-base-component";
import { ThreedHightlightRaycasterComponent } from "../threed-hightlight-raycaster-component";
import { ThreedUpdateSceneSettingsComponent } from "./threed-update-scene-settings-component";

export class ThreedUpdateViewSettingsComponent extends ThreedBaseComponent implements IThreedUpdatable {

    private scene: ThreedUpdateSceneSettingsComponent; 
    private cameraToUpdate?: IThreedPerspectiveCamera;

    constructor(cameraToUpdate?: IThreedPerspectiveCamera) {
        super();
        this.cameraToUpdate = cameraToUpdate;
    }

    override initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        this.scene = new ThreedUpdateSceneSettingsComponent(this.cameraToUpdate);
        this.scene.initialize(sceneManager);
    }

    onUpdateValues(values: any): void {
        if(!values) return;
        const settings: ThreedComplexOrbitWidgetSettings = values;
        if(!settings) return;

        this.scene.onUpdateValues(settings.threedSceneSettings);
        const raycasterComponent = this.sceneManager.getComponent(ThreedHightlightRaycasterComponent);
        raycasterComponent?.setHoveringColor(settings.hoverColor);
    }
}