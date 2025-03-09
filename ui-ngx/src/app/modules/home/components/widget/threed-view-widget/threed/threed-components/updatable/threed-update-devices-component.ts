

import { ThreedDevicesSettings } from "../../threed-models";
import { IThreedUpdatable } from "../ithreed-updatable";
import { ThreedBaseComponent } from "../threed-base-component";

export class ThreedUpdateDevicesComponent extends ThreedBaseComponent implements IThreedUpdatable {

    onUpdateValues(values: any): void {
        if (!values) return;
        const settings: ThreedDevicesSettings = values;
        if (!settings) return;
        const deviceGroupSettings = settings?.threedDeviceGroupSettings;
        if (!deviceGroupSettings) return;

        deviceGroupSettings.forEach(deviceGroup => {
            const objectsSettings = deviceGroup.threedObjectSettings;
            if (objectsSettings) {
                objectsSettings.forEach(objectSettings => {
                    this.sceneManager.modelManager.updateModelTransforms(objectSettings.entity.id, objectSettings);
                });
            }
        });
    }
}