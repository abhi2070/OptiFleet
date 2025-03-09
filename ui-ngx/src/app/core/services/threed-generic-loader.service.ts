/* eslint-disable @typescript-eslint/prefer-for-of */
import { Injectable } from '@angular/core';
import {
    ThreedDevicesSettings, ThreedEnvironmentSettings, ThreedSimpleOrbitWidgetSettings
} from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { IThreedSceneManager } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-managers/ithreed-scene-manager';
import { IAliasController } from '../public-api';
import { ThreedModelLoaderService, ThreedUniversalModelLoaderConfig } from './threed-model-loader.service';

@Injectable({
    providedIn: 'root'
})
export class ThreedGenericLoaderService {

    constructor(
        private threedModelLoader: ThreedModelLoaderService
    ) { }

    public loadSingleModel(
        settings: ThreedSimpleOrbitWidgetSettings,
        aliasName: string,
        aliasController: IAliasController,
        scene: IThreedSceneManager,
        id?: string,
        hasTooltip: boolean = true) {

        const config: ThreedUniversalModelLoaderConfig = {
            entityLoader: this.threedModelLoader.toEntityLoader2(settings, aliasName),
            aliasController
        };

        this.threedModelLoader.loadModelInScene(scene, config, id, hasTooltip);
    }

    public async loadEnvironment(
        settings: ThreedEnvironmentSettings,
        aliasController: IAliasController,
        scene: IThreedSceneManager,
        id?: string,
        hasTooltip: boolean = true
    ) {
        const config: ThreedUniversalModelLoaderConfig = {
            entityLoader: this.threedModelLoader.toEntityLoader(settings),
            aliasController
        };

        await this.threedModelLoader.loadModelInScene(scene, config, id, hasTooltip);
    }

    public async loadDevices(
        settings: ThreedDevicesSettings,
        aliasController: IAliasController,
        scene: IThreedSceneManager,
        hasTooltip: boolean = true
    ) {

        for (let i = 0; i < settings.threedDeviceGroupSettings.length; i++) {
            const deviceGroup = settings.threedDeviceGroupSettings[i];
            const loaders = this.threedModelLoader.toEntityLoaders(deviceGroup);

            for (let j = 0; j < loaders.length; j++) {
                const entityLoader = loaders[j];
                const config: ThreedUniversalModelLoaderConfig = {
                    entityLoader,
                    aliasController
                };

                await this.threedModelLoader.loadModelInScene(scene, config, undefined, hasTooltip);
            }
        }
    }
}
