

import { ThreedSceneSettings } from "../../threed-models";
import { IThreedSceneManager } from "../../threed-managers/ithreed-scene-manager";
import { IThreedPerspectiveCamera } from "../ithreed-perspective-camera";
import { IThreedUpdatable } from "../ithreed-updatable";
import { ThreedBaseComponent } from "../threed-base-component";
import { ThreedUpdateCameraComponent } from "./threed-update-camera-component";
import { ThreedUpdateDevicesComponent } from "./threed-update-devices-component";
import { ThreedUpdateEnvironmentComponent } from "./threed-update-environment-component";


export class ThreedUpdateSceneSettingsComponent extends ThreedBaseComponent implements IThreedUpdatable {

    private cameraToUpdate: IThreedPerspectiveCamera;
    
    private environment: ThreedUpdateEnvironmentComponent;
    private camera: ThreedUpdateCameraComponent;
    private devices: ThreedUpdateDevicesComponent;

    constructor(cameraToUpdate?: IThreedPerspectiveCamera) {
        super();
        this.cameraToUpdate = cameraToUpdate;
    }

    override initialize(sceneManager: IThreedSceneManager): void {
        super.initialize(sceneManager);

        this.environment = new ThreedUpdateEnvironmentComponent();
        this.camera = new ThreedUpdateCameraComponent(this.cameraToUpdate);
        this.devices = new ThreedUpdateDevicesComponent();

        this.environment.initialize(sceneManager);
        this.camera.initialize(sceneManager);
        this.devices.initialize(sceneManager);
    }

    onUpdateValues(values: any): void {

        //console.log("Update values ThreedUpdateSceneSettingsComponent", values);

        if(!values) return;
        const settings: ThreedSceneSettings = values;
        if(!settings) return;

        this.environment.onUpdateValues(settings.threedEnvironmentSettings);
        this.camera.onUpdateValues(settings.threedCameraSettings);
        this.devices.onUpdateValues(settings.threedDevicesSettings);
    }
}