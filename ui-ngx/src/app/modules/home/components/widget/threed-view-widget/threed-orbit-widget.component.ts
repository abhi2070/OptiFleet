/* eslint-disable max-len */
import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild, NgZone } from '@angular/core';
import { MatSliderChange } from '@angular/material/slider';
import { ThreedGenericLoaderService } from '@app/core/services/threed-generic-loader.service';
import { ACTIONS, ENVIRONMENT_ID, OBJECT_ID_TAG, ROOT_TAG } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-constants';
import {
  ThreedComplexOrbitWidgetSettings,
  ThreedSimpleOrbitWidgetSettings,
  isThreedComplexOrbitWidgetSettings,
  isThreedSimpleOrbitWidgetSettings
} from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { AppState } from '@core/core.state';
import { WidgetContext } from '@home/models/widget-component.models';
import { Store } from '@ngrx/store';
import { PageComponent } from '@shared/components/page.component';
import * as TWEEN from 'three/examples/jsm/libs/tween.module.js';
import { ThreedWidgetActionManager } from './threed-widget-action-manager';
import { ThreedWidgetDataUpdateManager } from './threed-widget-data-update-manager';
import { IThreedTester } from './threed/threed-components/ithreed-tester';
import { ThreedHightlightRaycasterComponent } from './threed/threed-components/threed-hightlight-raycaster-component';
import { ThreedOrbitControllerComponent } from './threed/threed-components/threed-orbit-controller-component';
import { ThreedGenericSceneManager } from './threed/threed-managers/threed-generic-scene-manager';
import { ThreedScenes } from './threed/threed-scenes/threed-scenes';
import { ThreedUtils } from './threed/threed-utils';
import * as THREE from 'three';


@Component({
  selector: 'tb-threed-orbit-widget',
  templateUrl: './threed-orbit-widget.component.html',
  styleUrls: ['./threed-orbit-widget.component.scss']
})
export class ThreedOrbitWidgetComponent extends PageComponent implements OnInit, AfterViewInit, OnDestroy {

  settings: ThreedSimpleOrbitWidgetSettings | ThreedComplexOrbitWidgetSettings;

  @Input()
  ctx: WidgetContext;

  @ViewChild('rendererContainer') rendererContainer?: ElementRef;

  private orbitScene: ThreedGenericSceneManager;
  private actionManager: ThreedWidgetActionManager;
  private dataUpdateManager: ThreedWidgetDataUpdateManager;

  public activeMode = 'selection';
  public explodedView = false;
  public animating = false;
  private lastExplodeFactorValue = 0;
  private currentExplodedObjectId?: string;

  public orbitType: 'simple' | 'complex' = 'simple';

  private readonly DEFAULT_MODEL_ID = 'DefaultModelId';


  constructor(
    protected store: Store<AppState>,
    protected cd: ChangeDetectorRef,
    private ngZone: NgZone,
    private threedLoader: ThreedGenericLoaderService
  ) {
    super(store);
  }

  async ngOnInit() {
    this.ctx.$scope.threedOrbitWidget = this;
    this.settings = this.ctx.settings;

    this.initializeManagers();

    const promises: Promise<any>[] = [];

    if (isThreedSimpleOrbitWidgetSettings(this.settings)) {
      this.orbitType = 'simple';
      this.orbitScene = ThreedScenes.createSimpleOrbitScene();
      this.loadSingleModel(this.settings);
    } else if (isThreedComplexOrbitWidgetSettings(this.settings)) {
      this.orbitType = 'complex';
      this.orbitScene = ThreedScenes.createComplexOrbitScene();

      promises.push(this.loadEnvironment(this.settings));
      promises.push(this.loadDevices(this.settings));
      //await this.loadEnvironment(this.settings);
      //await this.loadDevices(this.settings);
    } else {
      console.error('Orbit Settings not valid...', this.settings);
    }

    this.ngZone.runOutsideAngular(async () => {
      await Promise.all(promises);
      this.orbitScene.setValues(this.settings);
      this.onEditModeChanged();
      this.onDataUpdated();
    });
  }

  ngOnDestroy(): void {
    this.orbitScene?.destroy();
  }

  private initializeManagers() {
    this.actionManager = new ThreedWidgetActionManager(this.ctx);
    this.actionManager.createActions(ACTIONS.tooltip);
    this.dataUpdateManager = new ThreedWidgetDataUpdateManager(this.ctx, this.actionManager);
  }

  private loadSingleModel(settings: ThreedSimpleOrbitWidgetSettings) {
    if (this.ctx.datasources && this.ctx.datasources[0]) {
      const datasource = this.ctx.datasources[0];
      this.threedLoader.loadSingleModel(settings, datasource.aliasName, this.ctx.aliasController, this.orbitScene, this.DEFAULT_MODEL_ID);
    }
  }

  private async loadEnvironment(settings: ThreedComplexOrbitWidgetSettings) {
    await this.threedLoader.loadEnvironment(settings.threedSceneSettings.threedEnvironmentSettings, this.ctx.aliasController, this.orbitScene, ENVIRONMENT_ID, false);
  }

  private async loadDevices(settings: ThreedComplexOrbitWidgetSettings) {
    await this.threedLoader.loadDevices(settings.threedSceneSettings.threedDevicesSettings, this.ctx.aliasController, this.orbitScene);
  }

  ngAfterViewInit() {
    this.orbitScene.attachToElement(this.rendererContainer);
  }


  public onDataUpdated() {
    if (this.orbitType === 'simple') { return; }

    this.dataUpdateManager.onDataUpdate((this.settings as ThreedComplexOrbitWidgetSettings).threedSceneSettings?.threedDevicesSettings, this.orbitScene);
  }

  public onEditModeChanged() {
    this.orbitScene.active = !this.ctx.isEdit;
  }

  public toggleExplodedView() {
    this.explodedView = !this.explodedView;

    const raycastComponent = this.orbitScene.getComponent(ThreedHightlightRaycasterComponent);
    if (raycastComponent) {
      raycastComponent.raycastEnabled = !this.explodedView && this.activeMode === 'selection';
    }

    if (!this.explodedView && this.lastExplodeFactorValue > 0) {
      this.animating = true;
      const duration = 300;
      const fromValue = this.lastExplodeFactorValue;
      const toValue = 0;
      new TWEEN.Tween({ value: fromValue })
        .to({ value: toValue }, duration)
        .onUpdate((update: { value: number }) => {
          this.orbitScene.modelManager.explodeObjectByDistance(this.currentExplodedObjectId || this.DEFAULT_MODEL_ID, update.value);
        })
        .onComplete(() => {
          this.lastExplodeFactorValue = 0;
          this.animating = false;
          this.currentExplodedObjectId = undefined;
          this.cd.detectChanges();
        })
        .start();
    }
  }

  public explodeFactorChange(e: MatSliderChange) {
    this.lastExplodeFactorValue = e.value;

    const selectorComponents = this.orbitScene.findComponentsByTester(IThreedTester.isIThreedObjectSelector);
    let selectedObject: THREE.Object3D | undefined;
    for (const c of selectorComponents) {
      selectedObject = c.getSelectedObject();
      if (selectedObject) { break; }
    }

    let objectId = this.DEFAULT_MODEL_ID;
    if (selectedObject) {
      const root = ThreedUtils.findParentByChild(selectedObject, ROOT_TAG, true);
      objectId = root?.userData[OBJECT_ID_TAG] || objectId;
    }

    this.currentExplodedObjectId = objectId;
    this.orbitScene.modelManager.explodeObjectByDistance(objectId, e.value);
  }

  public focusOnObject() {
    this.orbitScene.getComponent(ThreedOrbitControllerComponent).focusOnObject(undefined, 500);
  }

  public clickMode(mode: 'selection' | 'pan') {
    this.activeMode = mode;
    const raycastComponent = this.orbitScene.getComponent(ThreedHightlightRaycasterComponent);
    if (raycastComponent) { raycastComponent.raycastEnabled = mode === 'selection'; }
  }

  public deselectObject() {
    const selectors = this.orbitScene.findComponentsByTester(IThreedTester.isIThreedObjectSelector);
    selectors.forEach(s => s.deselectObject());
  }

  public onResize(width: number, height: number): void {
    this.orbitScene.resize(width - 2, height - 2);
  }
}

