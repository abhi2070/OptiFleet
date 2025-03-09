import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, forwardRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AssetModel, ScriptModel, SimulationState, ThreedSimulationWidgetSettings } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { EntityAliases, EntityInfo, FormattedData, PageComponent } from '../public-api';
import { Threed } from '@app/modules/home/components/widget/threed-view-widget/threed/threed';
import { ThreedGenericSceneManager } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-managers/threed-generic-scene-manager';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { AttributeService, IAliasController } from '@app/core/public-api';
import { MatDialog } from '@angular/material/dialog';
import { ThreedScenes } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-scenes/threed-scenes';
import { Subscription } from 'rxjs';
import * as THREE from 'three';
import { ThreedWebRenderer } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-managers/threed-web-renderer';
import { ThreedVrControllerComponent } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-components/threed-vr-controller-component';
import { ThreedDynamicMenuDialogComponent } from '@app/modules/home/components/widget/lib/settings/threed/threed-dynamic-menu-dialog.component';
import { ThreedFirstPersonControllerComponent } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-components/threed-first-person-controller-component';
import { NG_VALUE_ACCESSOR } from '@angular/forms';

interface CompiledScriptModel extends ScriptModel {
  executeFnc: Function;
}

@Component({
  selector: 'tb-simulation-helper',
  templateUrl: './simulation-helper.component.html',
  styleUrls: ['./simulation-helper.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SimulationHelperComponent),
      multi: true
    }
  ]
})
export class SimulationHelperComponent extends PageComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('rendererContainer') rendererContainer?: ElementRef;

  @Input()
  public aliasController: IAliasController;

  @Input()
  public settings: ThreedSimulationWidgetSettings;

  @Input()
  public editor: boolean;

  private context: any;
  public simulationScene: ThreedGenericSceneManager;
  public simulationState: SimulationState = SimulationState.UNCOMPILED;
  public SimulationState = SimulationState;
  private timeHandler: NodeJS.Timeout;
  public time = 0;
  private menuData: any = {};
  private previousVRGripSub?: Subscription;

  constructor(
    protected store: Store<AppState>,
    private cd: ChangeDetectorRef,
    private dialog: MatDialog,
    private attributeService: AttributeService) {
    super(store);
  }

  ngOnInit(): void {
    this.createSimulationScene();
  }

  ngAfterViewInit(): void {
    this.simulationScene?.attachToElement(this.rendererContainer);
  }

  ngOnDestroy(): void {
    this.simulationScene?.destroy();
  }

  public updateSettings(settings: ThreedSimulationWidgetSettings) {
    this.settings = settings;
  }

  public onDataUpdate(data: FormattedData[]) {
    if (this.simulationState !== SimulationState.STARTED) { return; }
    if (!this.context || !this.context.scripts || !this.context.scripts['onDataUpdate.js']) { return; }

    try {
      const result = this.context.scripts['onDataUpdate.js'].executeFnc(this.context, this.simulationScene, Threed, data);
    } catch (error) {
    }
  }

  public onEditModeChanged(isEdit: boolean) {
    if (this.simulationScene) { this.simulationScene.active = isEdit; }
  }

  public onResize(width: number, height: number): void {
    this.simulationScene?.resize(width - 2, height - 2);
  }

  public async stopSimulation() {
    clearInterval(this.timeHandler);
    this.simulationState = SimulationState.UNCOMPILED;
    this.time = 0;

    try {
      const result = this.context.scripts['stop.js'].executeFnc(this.context, this.simulationScene, Threed);
      if (result instanceof Promise) { await result; }
    } catch (error) {
    }

    this.simulationScene?.destroy();
    this.simulationScene = undefined;
  }

  public async resetSimulation() {
    try {
      const result = this.context.scripts['reset.js'].executeFnc(this.context, this.simulationScene, Threed);
      if (result instanceof Promise) { await result; }
    } catch (error) {
    }
  }

  public createSimulationScene() {
    this.simulationScene?.destroy();
    this.simulationScene = ThreedScenes.createSimulationScene();
    this.previousVRGripSub?.unsubscribe();
    this.previousVRGripSub = this.simulationScene.getComponent(ThreedVrControllerComponent).onGripPressed.subscribe(_ => {

      if (this.simulationState !== SimulationState.STARTED) {
        this.startSimulation();
        this.simulationScene.cssManager.createVRText('Simulation started', new THREE.Vector3(0, 0, -2), true, '', .2, 2);
      }
      else {
        this.simulationScene.getTRenderer(ThreedWebRenderer).getRenderer().xr.getSession().end();
        this.stopSimulation();
        this.cd.detectChanges();
      }
    });
    if (this.rendererContainer) { this.simulationScene.attachToElement(this.rendererContainer); }
  }

  public async startSimulation() {
    if (this.simulationState === SimulationState.STARTED) { return; }

    await this.compile();

    try {
      const result = this.context.scripts['start.js'].executeFnc(this.context, this.simulationScene, Threed);
      if (result instanceof Promise) { await result; }
      this.simulationState = SimulationState.STARTED;
    } catch (error) {
      this.simulationState = SimulationState.UNCOMPILED;
    }

    const millis = 200;
    const timeUUID = 'my-time-mesh-uuid';
    this.time = 0;
    this.timeHandler = setInterval(() => {
      this.time += millis / 1000;
      this.time = Number(this.time.toFixed(2));

      this.simulationScene.cssManager.createOrUpdateVRText('Time: ' + this.time, new THREE.Vector3(0, .8, -2), false, '#000', .1, timeUUID);

      this.cd.detectChanges();
    }, millis);
  }

  public async compile() {
    if (this.simulationState === SimulationState.SETUP_DONE || this.simulationState === SimulationState.STARTED) { return; }

    this.simulationState = SimulationState.COMPILING;
    const a = this.settings.assets;
    const s = this.settings.scripts;
    const assets: { [key: string]: AssetModel } = this.toDictionary(a, i => i.name);
    const entities = await this.getEntities();
    const scripts = this.getCompiledScripts(s);
    const services = this.getServices();
    this.context = { assets, entities, scripts, userData: {}, menuData: this.menuData, services };

    try {
      this.createSimulationScene();
      const result = scripts['setup.js'].executeFnc(this.context, this.simulationScene, Threed);
      if (result instanceof Promise) { await result; }
      this.simulationState = SimulationState.SETUP_DONE;
      this.cd.detectChanges();
    } catch (error) {
      this.simulationState = SimulationState.UNCOMPILED;
    }
  }

  private toDictionary<T>(
    arr: T[],
    keySelector: (item: T) => string
  ): { [key: string]: T } {
    return arr.reduce((obj: { [key: string]: T }, item: T) => {
      const key = keySelector(item);
      obj[key] = item;
      return obj;
    }, {});
  }

  private async getEntities() {
    const entities: {
      entityAliases: EntityAliases;
      entityAliasesList: { alias: string; id: string }[];
      entityInfos: EntityInfo[];
    } = {
      entityAliases: this.aliasController.getEntityAliases(),
      entityAliasesList: [],
      entityInfos: []
    };
    for (const aliasId of Object.keys(entities.entityAliases)) {
      entities.entityAliasesList.push({
        alias: entities.entityAliases[aliasId].alias,
        id: aliasId
      });
      const entityInfo = await this.aliasController.resolveEntitiesInfo(aliasId).toPromise();
      if (Array.isArray(entityInfo)) { entityInfo.forEach(e => entities.entityInfos.push(e)); }
      else { entities.entityInfos.push(entityInfo); }
    }
    return entities;
  }

  private getCompiledScripts(rawScripts: ScriptModel[]): { [key: string]: CompiledScriptModel } {
    const scripts: { [key: string]: CompiledScriptModel } = {};
    for (const script of rawScripts) {
      const functionRef = script.name !== 'onDataUpdate.js' ?
        new Function('context', 'simulationScene', 'Threed', script.body) :
        new Function('context', 'simulationScene', 'Threed', 'datasources', script.body);
      scripts[script.name] = {
        body: script.body,
        deletable: script.deletable,
        name: script.name,
        executeFnc: functionRef
      };
    }
    return scripts;
  }

  private getServices() {
    return {
      attributeService: this.attributeService,
      aliasController: this.aliasController
    };
  }

  public lockCursor() {
    this.simulationScene?.getComponent(ThreedFirstPersonControllerComponent)?.lockControls();
  }

  public openOptionsMenu() {
    const html = this.settings.menuHtml;
    const css = this.settings.menuHtml;
    const jsBody = this.settings.menuJs;
    const fnc = new Function('context', 'simulationScene', 'Threed', jsBody);
    this.dialog.open(ThreedDynamicMenuDialogComponent, {
      width: '50%',
      data: { html, css, js: { fnc, args: [this.context, this.simulationScene, Threed] } }
    });
  }

}
