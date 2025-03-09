

import { ENVIRONMENT_ID } from "../../threed-constants";
import { ThreedEnvironmentSettings } from "../../threed-models";
import { IThreedUpdatable } from "../ithreed-updatable";
import { ThreedBaseComponent } from "../threed-base-component";


export class ThreedUpdateEnvironmentComponent extends ThreedBaseComponent implements IThreedUpdatable {

    onUpdateValues(values: any): void {
        if(!values) return;
        const settings: ThreedEnvironmentSettings = values;
        if(!settings) return;
        const environmentSettings = settings?.objectSettings;
        if (!environmentSettings) return;

        this.sceneManager.modelManager.updateModelTransforms(ENVIRONMENT_ID, environmentSettings);
    }
}