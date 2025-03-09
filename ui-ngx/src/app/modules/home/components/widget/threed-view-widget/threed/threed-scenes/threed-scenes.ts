

import * as THREE from 'three';
import { ThreedCameraPreviewComponent } from "../threed-components/threed-camera-preview-component";
import { ThreedDefaultAmbientComponent } from "../threed-components/threed-default-ambient-component";
import { ThreedFirstPersonControllerComponent } from "../threed-components/threed-first-person-controller-component";
import { ThreedHightlightTooltipRaycasterComponent } from "../threed-components/threed-hightlight-tooltip-raycaster-component";
import { ThreedOrbitControllerComponent } from "../threed-components/threed-orbit-controller-component";
import { ThreedPerspectiveCameraComponent } from "../threed-components/threed-perspective-camera-component";
import { ThreedTransformControllerComponent } from "../threed-components/threed-transform-controller-component";
import { ThreedTransformRaycasterComponent } from "../threed-components/threed-transform-raycaster-component";
import { ThreedUpdateSceneSettingsComponent } from "../threed-components/updatable/threed-update-scene-settings-component";
import { ThreedUpdateViewSettingsComponent } from "../threed-components/updatable/threed-update-view-settings-component";
import { CAMERA_ID, OBJECT_ID_TAG } from '../threed-constants';
import { ThreedGenericSceneManager } from "../threed-managers/threed-generic-scene-manager";
import { ThreedSceneBuilder } from "./threed-scene-builder";
import { ThreedProgressBarComponent } from '../threed-components/threed-progress-bar-component';
import { ThreedVrControllerComponent } from '../threed-components/threed-vr-controller-component';
import { ThreedVrHightlightTooltipRaycasterComponent } from '../threed-components/threed-vr-hightlight-tooltip-raycaster-component';

export class ThreedScenes {

    //* INFO: Simple Orbit scene 
    public static createSimpleOrbitScene(): ThreedGenericSceneManager {
        const builder = new ThreedSceneBuilder({ shadow: true })
            .add(new ThreedPerspectiveCameraComponent())
            .add(new ThreedDefaultAmbientComponent(false))
            .add(new ThreedOrbitControllerComponent())
            .add(new ThreedUpdateViewSettingsComponent())
            .add(new ThreedProgressBarComponent());

        return builder.build();
    }

    //* INFO: Complex Orbit scene 
    public static createComplexOrbitScene(): ThreedGenericSceneManager {
        const builder = new ThreedSceneBuilder({ shadow: true })
            .add(new ThreedPerspectiveCameraComponent())
            .add(new ThreedDefaultAmbientComponent(false))
            .add(new ThreedOrbitControllerComponent())
            .add(new ThreedUpdateViewSettingsComponent())
            .add(new ThreedHightlightTooltipRaycasterComponent('click', 'root'))
            .add(new ThreedProgressBarComponent());
        //.add(new ThreedHightlightRaycasterComponent('click', 'root'));

        return builder.build();
    }

    //* INFO: Navigation scene 
    public static createNavigationScene(): ThreedGenericSceneManager {
        const cameraComponent = new ThreedPerspectiveCameraComponent();

        const builder = new ThreedSceneBuilder({ vr: true })
            .add(cameraComponent)
            .add(new ThreedDefaultAmbientComponent(true))
            .add(new ThreedFirstPersonControllerComponent())
            .add(new ThreedUpdateViewSettingsComponent(cameraComponent))
            .add(new ThreedProgressBarComponent())
            .add(new ThreedVrControllerComponent())
            .add(new ThreedVrHightlightTooltipRaycasterComponent('click', 'root', new THREE.Vector2()));

        return builder.build();
    }

    //* INFO: Simulation scene 
    public static createSimulationScene(): ThreedGenericSceneManager {
        const builder = new ThreedSceneBuilder({ vr: true, shadow: true })
            .add(new ThreedPerspectiveCameraComponent(new THREE.Vector3(0, 1.7, 0)))
            .add(new ThreedDefaultAmbientComponent(false))
            .add(new ThreedFirstPersonControllerComponent())
            .add(new ThreedVrControllerComponent("Start/Stop: Grip<br>"));
        return builder.build();
    }

    //* INFO: Editor scene for FPS
    public static createEditorSceneWithCameraDebug(): ThreedGenericSceneManager {
        const transformControllercomponent = new ThreedTransformControllerComponent(true);
        const cameraPreviewComponent = new ThreedCameraPreviewComponent();
        const builder = new ThreedSceneBuilder({ shadow: false })
            .add(new ThreedPerspectiveCameraComponent())
            .add(new ThreedDefaultAmbientComponent(true))
            .add(new ThreedOrbitControllerComponent())
            .add(transformControllercomponent)
            .add(new ThreedTransformRaycasterComponent('click', 'root', transformControllercomponent))
            .add(cameraPreviewComponent)
            .add(new ThreedUpdateSceneSettingsComponent(cameraPreviewComponent))
            .add(new ThreedProgressBarComponent())
            .addSubscription(
                transformControllercomponent.onChangeAttachTransformController.subscribe(model => {
                    cameraPreviewComponent.enabled = model ? model.userData[OBJECT_ID_TAG] == CAMERA_ID : false;
                })
            );

        return builder.build();
    }


    //* INFO: Editor scene for Complex Orbit
    public static createEditorSceneWithoutCameraDebug(): ThreedGenericSceneManager {
        const transformControllercomponent = new ThreedTransformControllerComponent(true);
        const builder = new ThreedSceneBuilder({ shadow: false })
            .add(new ThreedPerspectiveCameraComponent())
            .add(new ThreedDefaultAmbientComponent(true))
            .add(new ThreedOrbitControllerComponent())
            .add(transformControllercomponent)
            .add(new ThreedTransformRaycasterComponent('click', 'root', transformControllercomponent))
            .add(new ThreedUpdateSceneSettingsComponent())
            .add(new ThreedProgressBarComponent());

        return builder.build();
    }
}