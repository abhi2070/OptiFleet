

import { AfterContentChecked, AfterViewInit, Component, ElementRef, HostListener, Input, OnInit, Renderer2, ViewChild, forwardRef } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator
} from '@angular/forms';
import { IAliasController } from '@app/core/public-api';
import { ENVIRONMENT_ID, ThreedSceneControllerType } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-constants';
import {
  ThreedDeviceGroupSettings,
  ThreedDevicesSettings,
  ThreedEnvironmentSettings,
  ThreedSceneSettings,
} from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { AppState } from '@core/core.state';
import { EntityAliasAttribute, ModelUrl, ThreedModelLoaderService, ThreedUniversalModelLoaderConfig } from '@core/services/threed-model-loader.service';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { PageComponent } from '@shared/components/page.component';
import { ThreedOrbitControllerComponent } from '../../../threed-view-widget/threed/threed-components/threed-orbit-controller-component';
import { ThreedTransformControllerComponent } from '../../../threed-view-widget/threed/threed-components/threed-transform-controller-component';
import { ThreedGenericSceneManager } from '../../../threed-view-widget/threed/threed-managers/threed-generic-scene-manager';
import { ThreedScenes } from '../../../threed-view-widget/threed/threed-scenes/threed-scenes';
import { IThreedExpandable } from './ithreed-expandable';


@Component({
  selector: 'tb-threed-scene-settings',
  templateUrl: './threed-scene-settings.component.html',
  styleUrls: ['./../widget-settings.scss', './threed-scene-settings.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ThreedSceneSettingsComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ThreedSceneSettingsComponent),
      multi: true
    }
  ]
})
export class ThreedSceneSettingsComponent extends PageComponent implements OnInit, AfterViewInit, ControlValueAccessor, Validator, AfterContentChecked {

  @Input()
  disabled: boolean;

  @Input()
  aliasController: IAliasController;

  @Input()
  sceneControllerType: ThreedSceneControllerType;

  @ViewChild('rendererContainer') rendererContainer?: ElementRef;

  @ViewChild('threedEnvironmentSettings') threedEnvironmentSettings?: IThreedExpandable;
  @ViewChild('threedCameraSettings') threedCameraSettings?: IThreedExpandable;
  @ViewChild('threedDevicesSettings') threedDevicesSettings?: IThreedExpandable;

  private modelValue: ThreedSceneSettings;

  private propagateChange = null;

  public threedSceneSettingsFormGroup: FormGroup;

  public sceneEditor: ThreedGenericSceneManager;
  private isVisible: boolean = false;
  fullscreen: boolean = false;
  loadingProgress = 100;

  private lastEntityLoaders: Map<string, ModelUrl | EntityAliasAttribute> = new Map();

  constructor(protected store: Store<AppState>,
    private translate: TranslateService,
    private fb: FormBuilder,
    private threedModelLoader: ThreedModelLoaderService,
    private renderer2: Renderer2) {
    super(store);

  }

  ngOnInit(): void {
    if (this.hasCamera()) {
      this.sceneEditor = ThreedScenes.createEditorSceneWithCameraDebug();
    } else {
      this.sceneEditor = ThreedScenes.createEditorSceneWithoutCameraDebug();
    }

    const controlsConfig = this.hasCamera() ? {
      threedEnvironmentSettings: [null, []],
      threedCameraSettings: [null, []],
      threedDevicesSettings: [null, []],
    } : {
      threedEnvironmentSettings: [null, []],
      threedDevicesSettings: [null, []],
    };
    this.threedSceneSettingsFormGroup = this.fb.group(controlsConfig);

    this.threedSceneSettingsFormGroup.get("threedDevicesSettings").valueChanges.subscribe((newValues: ThreedDevicesSettings) => {
      this.updateSceneModels(newValues);
    });

    this.threedSceneSettingsFormGroup.get("threedEnvironmentSettings").valueChanges.subscribe((newValue: ThreedEnvironmentSettings) => {
      this.updateSceneModels(newValue);
    });

    this.threedSceneSettingsFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });

    const transformComponent = this.sceneEditor.getComponent(ThreedTransformControllerComponent);
    transformComponent.modelSelected.subscribe(id => this.forceExpand(id));
  }

  public hasCamera(): boolean {
    return this.sceneControllerType != ThreedSceneControllerType.ORBIT_CONTROLLER;
  }

  private loadModel(config: ThreedUniversalModelLoaderConfig, id?: string) {
    if (!config.entityLoader) return;

    this.threedModelLoader.loadModelAsGLTF(config, { updateProgress: p => this.loadingProgress = p * 100 }).subscribe(res => {
      this.sceneEditor.modelManager.replaceModel(res.model, { id: id ? id : res.entityId });
    });
  }

  ngAfterContentChecked(): void {
    if (this.isVisible == false && this.rendererContainer?.nativeElement.offsetParent != null) {
      this.isVisible = true;
      this.detectResize();
    }
    else if (this.isVisible == true && this.rendererContainer?.nativeElement.offsetParent == null) {
      this.sceneEditor.getComponent(ThreedTransformControllerComponent).deselectObject();
      this.isVisible = false;
    }
  }

  ngAfterViewInit(): void {
    this.sceneEditor.attachToElement(this.rendererContainer);
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  validate(control: AbstractControl): ValidationErrors {
    return this.threedSceneSettingsFormGroup.valid ? null : {
      threedSceneSettings: {
        valid: false,
      },
    };
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.threedSceneSettingsFormGroup.disable({ emitEvent: false });
    } else {
      this.threedSceneSettingsFormGroup.enable({ emitEvent: false });
    }
  }

  writeValue(value: ThreedSceneSettings): void {
    this.modelValue = value;

    this.sceneEditor.setValues(this.modelValue);
    this.threedSceneSettingsFormGroup.patchValue(
      this.modelValue, { emitEvent: false }
    );

    this.updateSceneModels(value.threedEnvironmentSettings);
    this.updateSceneModels(value.threedDevicesSettings);
  }

  private updateModel() {
    const value = this.threedSceneSettingsFormGroup.value;
    this.modelValue = value;

    this.sceneEditor.setValues(this.modelValue);

    this.propagateChange(this.modelValue);
  }

  private updateSceneModels(newSettings: ThreedDevicesSettings | ThreedEnvironmentSettings) {
    if (newSettings == null) return;

    if ("threedDeviceGroupSettings" in newSettings) {
      let deletedObjects = Array.from(this.lastEntityLoaders.keys()).filter(id => id != ENVIRONMENT_ID);

      newSettings.threedDeviceGroupSettings.forEach((deviceGroup: ThreedDeviceGroupSettings) => {
        const loaders = this.threedModelLoader.toEntityLoaders(deviceGroup);
        loaders?.forEach(entityLoader => {
          const config: ThreedUniversalModelLoaderConfig = {
            entityLoader,
            aliasController: this.aliasController
          }

          const id = entityLoader.entity.id;
          const lastEntityLoader = this.lastEntityLoaders.get(id);
          if (!this.threedModelLoader.areLoaderEqual(lastEntityLoader, entityLoader)) {
            this.lastEntityLoaders.set(id, entityLoader);
            this.loadModel(config);
          }

          const index = deletedObjects.indexOf(id);
          if (index >= 0) deletedObjects.splice(index, 1);
        })
      });

      deletedObjects.forEach(id => this.sceneEditor.modelManager.removeModel(id));
    } else {
      const entityLoader = this.threedModelLoader.toEntityLoader(newSettings);
      const config: ThreedUniversalModelLoaderConfig = {
        entityLoader,
        aliasController: this.aliasController
      }

      const id = ENVIRONMENT_ID;
      const lastEntityLoader = this.lastEntityLoaders.get(id);
      if (!this.threedModelLoader.areLoaderEqual(lastEntityLoader, entityLoader)) {
        this.lastEntityLoaders.set(id, entityLoader);
        this.loadModel(config, id);
      }
    }
  }

  public enterFullscreen() {
    this.rendererContainer.nativeElement.requestFullscreen();
    this.fullscreen = true;
  }

  public exitFullscreen() {
    if (document.fullscreenElement) {
      document.exitFullscreen();
      this.onExitFullscreen();
    }
  }

  private onExitFullscreen() {
    this.renderer2.addClass(this.rendererContainer.nativeElement, "zero-size");
    this.sceneEditor?.resize();
    setTimeout(() => {
      this.renderer2.removeClass(this.rendererContainer.nativeElement, "zero-size");
      this.sceneEditor?.resize();
    }, 50);
    this.fullscreen = false;
  }

  public changeControlMode(mode: "translate" | "rotate" | "scale") {
    this.sceneEditor?.getComponent(ThreedTransformControllerComponent).changeTransformControllerMode(mode);
  }

  public focusOnObject() {
    this.sceneEditor?.getComponent(ThreedOrbitControllerComponent).focusOnObject();
  }

  @HostListener('window:resize')
  public detectResize(): void {
    this.sceneEditor?.resize();
    setTimeout(() => {
      this.sceneEditor?.resize();
    }, 1000);
  }

  @HostListener('document:fullscreenchange', ['$event'])
  @HostListener('document:webkitfullscreenchange', ['$event'])
  @HostListener('document:mozfullscreenchange', ['$event'])
  @HostListener('document:MSFullscreenChange', ['$event'])
  onFullscreenChange(event: any) {
    if (!document.fullscreenElement) {
      this.onExitFullscreen();
    }
  }

  private forceExpand(id: string): void {
    this.threedEnvironmentSettings?.forceExpand(id);
    this.threedCameraSettings?.forceExpand(id);
    this.threedDevicesSettings?.forceExpand(id);
  }
}