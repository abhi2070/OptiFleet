

import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild, OnDestroy, HostBinding, NgZone } from '@angular/core';
import { ThreedGenericLoaderService } from '@app/core/services/threed-generic-loader.service';
import { ACTIONS, ENVIRONMENT_ID } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-constants';
import { ThreedViewWidgetSettings } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { AppState } from '@core/core.state';
import { WidgetContext } from '@home/models/widget-component.models';
import { Store } from '@ngrx/store';
import { PageComponent } from '@shared/components/page.component';
import { ThreedWidgetActionManager } from './threed-widget-action-manager';
import { ThreedWidgetDataUpdateManager } from './threed-widget-data-update-manager';
import { ThreedFirstPersonControllerComponent } from './threed/threed-components/threed-first-person-controller-component';
import { ThreedGenericSceneManager } from './threed/threed-managers/threed-generic-scene-manager';
import { ThreedScenes } from './threed/threed-scenes/threed-scenes';
import { Subscription } from 'rxjs';


@Component({
  selector: 'tb-threed-navigation-widget',
  templateUrl: './threed-navigation-widget.component.html',
  styleUrls: ['./threed-navigation-widget.component.scss']
})
export class ThreedNavigationWidgetComponent extends PageComponent implements OnInit, AfterViewInit, OnDestroy {

  @HostBinding('style.overflow') overflow = 'hidden';

  settings: ThreedViewWidgetSettings;

  @Input()
  ctx: WidgetContext;

  @ViewChild('rendererContainer') rendererContainer?: ElementRef;

  public pointerLocked: boolean = false;

  private navigationScene: ThreedGenericSceneManager;
  private actionManager: ThreedWidgetActionManager;
  private dataUpdateManager: ThreedWidgetDataUpdateManager;
  private pointerLockedSub: Subscription;

  constructor(
    protected store: Store<AppState>,
    protected cd: ChangeDetectorRef,
    private ngZone: NgZone,
    private threedLoader: ThreedGenericLoaderService
  ) {
    super(store);

    this.navigationScene = ThreedScenes.createNavigationScene();
  }

  async ngOnInit() {
    this.ctx.$scope.threedNavigationWidget = this;
    this.settings = this.ctx.settings;

    this.initializeManagers();

    if (!this.settings.hoverColor || !this.settings.threedSceneSettings) {
      return;
    }

    this.navigationScene.setValues(this.settings);
    this.pointerLockedSub = this.navigationScene.getComponent(ThreedFirstPersonControllerComponent)?.onPointerLockedChanged.subscribe(v => {
      this.pointerLocked = v;
      this.cd.detectChanges();
    });

    this.ngZone.runOutsideAngular(async () => {
      await this.loadModels();
      this.onEditModeChanged();
      this.onDataUpdated();
    });
  }

  ngOnDestroy(): void {
    this.pointerLockedSub?.unsubscribe();
    this.navigationScene?.destroy();
  }
  
  private initializeManagers() {
    this.actionManager = new ThreedWidgetActionManager(this.ctx);
    this.actionManager.createActions(ACTIONS.tooltip);
    this.dataUpdateManager = new ThreedWidgetDataUpdateManager(this.ctx, this.actionManager);
  }

  private async loadModels() {
    await this.loadEnvironment();
    await this.loadDevices();
  }

  private async loadEnvironment() {
    await this.threedLoader.loadEnvironment(this.settings.threedSceneSettings.threedEnvironmentSettings, this.ctx.aliasController, this.navigationScene, ENVIRONMENT_ID, false);
  }

  private async loadDevices() {
    await this.threedLoader.loadDevices(this.settings.threedSceneSettings.threedDevicesSettings, this.ctx.aliasController, this.navigationScene);
  }

  ngAfterViewInit() {
    this.navigationScene.attachToElement(this.rendererContainer);
  }

  lockCursor(event: Event) {
    event.stopPropagation(); 
    this.navigationScene.getComponent(ThreedFirstPersonControllerComponent)?.lockControls();
  }

  public onDataUpdated() {
    this.dataUpdateManager.onDataUpdate(this.settings.threedSceneSettings?.threedDevicesSettings, this.navigationScene);
  }

  public onEditModeChanged() {
    this.navigationScene.active = !this.ctx.isEdit;
  }

  public onResize(width: number, height: number): void {
    this.navigationScene?.resize(width-2, height-2);
  }
}