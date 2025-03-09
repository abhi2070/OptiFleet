

import { WidgetContext } from "@app/modules/home/models/widget-component.models";
import { FormattedData } from "@app/shared/public-api";
import {
    fillDataPattern,
    formattedDataFormDatasourceData,
    mergeFormattedData,
    parseFunction,
    processDataPattern,
    safeExecute
} from '@core/utils';
import { parseWithTranslation } from '../lib/maps/common-maps-utils';
import { MarkerImageInfo } from "../lib/maps/map-models";
import { ThreedWidgetActionManager } from "./threed-widget-action-manager";
import { ACTIONS } from "./threed/threed-constants";
import { IThreedSceneManager } from "./threed/threed-managers/ithreed-scene-manager";
import { ThreedDevicesSettings, ThreedMarkerSettings, ThreedTooltipSettings } from "./threed/threed-models";
import { TranslateService } from '@ngx-translate/core';
import { UtilsService } from '@core/services/utils.service';


export class ThreedWidgetDataUpdateManager {

    private readonly ctx: WidgetContext;
    private readonly actionManager: ThreedWidgetActionManager;

    constructor(
        ctx: WidgetContext,
        actionManager: ThreedWidgetActionManager
    ) {
        this.ctx = ctx;
        this.actionManager = actionManager;
    }

    public onDataUpdate(settings: ThreedDevicesSettings, scene: IThreedSceneManager) {
        if (!settings || !scene) return;

        const data = this.ctx.data;
        let formattedData = formattedDataFormDatasourceData(data);
        if (this.ctx.latestData && this.ctx.latestData.length) {
            const formattedLatestData = formattedDataFormDatasourceData(this.ctx.latestData);
            formattedData = mergeFormattedData(formattedData, formattedLatestData);
        }

        // We associate the new data with the tooltip settings, according to the entity alias
        formattedData.forEach(fd => {
            settings.threedDeviceGroupSettings?.forEach(deviceGroup => {
                if (deviceGroup.threedEntityAliasSettings.entityAlias == fd.aliasName) {

                    if (deviceGroup.threedMarkerSettings?.showMarker) {
                        this.updateMarker(deviceGroup.threedMarkerSettings, formattedData, fd, scene);
                    }

                    if (deviceGroup.threedTooltipSettings.showTooltip) {
                        this.updateTooltip(deviceGroup.threedTooltipSettings, fd, scene);
                    }
                }

            });
        });

        /*
        // We associate the new data with the tooltip settings, according to the entity alias
        formattedData.forEach(fd => {
            settings.threedDeviceGroupSettings?.forEach(deviceGroup => {
    
                if (deviceGroup.threedMarkerSettings?.showMarker) {
                    this.updateMarker(deviceGroup.threedMarkerSettings, formattedData);
                }
    
                if (deviceGroup.threedTooltipSettings.showTooltip) {
                    if (deviceGroup.threedEntityAliasSettings.entityAlias == fd.aliasName) {
                        const pattern = deviceGroup.threedTooltipSettings.tooltipPattern;
                        const tooltipText = parseWithTranslation.prepareProcessPattern(pattern, true);
                        const replaceInfoTooltipMarker = processDataPattern(tooltipText, fd);
                        const content = fillDataPattern(tooltipText, replaceInfoTooltipMarker, fd);
    
                        const tooltip = scene.cssManager.updateLabelContent([fd.entityId], content);
                        if (tooltip) this.actionManager.bindPopupActions(tooltip.divElement, fd.$datasource, ACTIONS.tooltip);
                    }
                }
            });
        });*/
    }


    private updateTooltip(settings: ThreedTooltipSettings, fd: FormattedData, scene: IThreedSceneManager) {
        try {
            const pattern = settings.tooltipPattern;

            if(!parseWithTranslation.translateFn){
                const translate = (key: string, defaultTranslation?: string): string => {
                    if (key) {
                      return (this.ctx.$injector.get(UtilsService).customTranslation(key, defaultTranslation || key)
                        || this.ctx.$injector.get(TranslateService).instant(key));
                    }
                    return '';
                  }
                parseWithTranslation.setTranslate(translate);
            }
            const tooltipText = parseWithTranslation.prepareProcessPattern(pattern);
            const replaceInfoTooltipMarker = processDataPattern(tooltipText, fd);
            const content = fillDataPattern(tooltipText, replaceInfoTooltipMarker, fd);

            const tooltip = scene.cssManager.updateLabel([fd.entityId], content);
            if (tooltip) {
                this.actionManager.bindPopupActions(tooltip.htmlElement, fd.$datasource, ACTIONS.tooltip);
                if (tooltip.vrMesh) {
                    this.actionManager.bindMeshActions(tooltip.vrMesh, fd.$datasource, ACTIONS.tooltip);
                }
            }
        } catch (_) { }
    }

    private updateMarker(settings: ThreedMarkerSettings, markersData: FormattedData[], data: FormattedData, scene: IThreedSceneManager) {

        const parsedMarkerImageFunction = parseFunction(settings.markerImageFunction, ['data', 'images', 'dsData', 'dsIndex']);
        const image = (settings.markerImage?.length) ? {
            url: settings.markerImage,
            size: settings.markerImageSize || 34
        } : null;

        const currentImage: MarkerImageInfo = settings.useMarkerImageFunction ?
            safeExecute(parsedMarkerImageFunction,
                [data, settings.markerImages, markersData, data.dsIndex]) : image;
        //const imageSize = `height: ${settings.markerImageSize || 34}px; width: ${settings.markerImageSize || 34}px;`;
        //const style = currentImage ? 'background-image: url(' + currentImage.url + '); ' + imageSize : '';

        scene.cssManager.updateImage([data.entityId], currentImage);
    }

    /*
    private updateMarker(settings: ThreedMarkerSettings, markersData: FormattedData[]) {

        const parsedMarkerImageFunction = parseFunction(settings.markerImageFunction, ['data', 'images', 'dsData', 'dsIndex']);
        const image = (settings.markerImage?.length) ? {
            url: settings.markerImage,
            size: settings.markerImageSize || 34
        } : null;


        markersData.forEach(data => {
            const currentImage: MarkerImageInfo = settings.useMarkerImageFunction ?
                safeExecute(parsedMarkerImageFunction,
                    [data, settings.markerImages, markersData, data.dsIndex]) : image;
            const imageSize = `height: ${settings.markerImageSize || 34}px; width: ${settings.markerImageSize || 34}px;`;
            const style = currentImage ? 'background-image: url(' + currentImage.url + '); ' + imageSize : '';

            console.log(currentImage, style);

            settings.icon = {
                icon: L.divIcon({
                    html: `<div class="arrow"
               style="transform: translate(-10px, -10px)
               rotate(${data.rotationAngle}deg);
               ${style}"><div>`
                }), size: [30, 30]
            };
        });
    }*/
} 