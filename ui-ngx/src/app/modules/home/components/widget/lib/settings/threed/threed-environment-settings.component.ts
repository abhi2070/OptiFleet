import { Component, forwardRef, Input, OnInit, ViewChild, EventEmitter, Output } from '@angular/core';
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
import { AppState } from '@core/core.state';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { PageComponent } from '@shared/components/page.component';
import { ThreedGenericSceneManager } from '../../../threed-view-widget/threed/threed-managers/threed-generic-scene-manager';
import { ThreedEntityAliasSettings } from './aliases/threed-entity-alias-settings.component';
import { ThreedEntityKeySettings, ThreedEntityKeySettingsComponent } from './aliases/threed-entity-key-settings.component';
import { IThreedExpandable } from './ithreed-expandable';
import { ThreedObjectSettingsComponent } from './threed-object-settings.component';
import { defaultThreedVectorOneSettings, defaultThreedVectorZeroSettings, ThreedEnvironmentSettings } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { ENVIRONMENT_ID } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-constants';
import { ThreedTransformControllerComponent } from '../../../threed-view-widget/threed/threed-components/threed-transform-controller-component';


@Component({
  selector: 'tb-threed-environment-settings',
  templateUrl: './threed-environment-settings.component.html',
  styleUrls: ['./../widget-settings.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ThreedEnvironmentSettingsComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ThreedEnvironmentSettingsComponent),
      multi: true
    }
  ]
})

export class ThreedEnvironmentSettingsComponent extends PageComponent implements OnInit, ControlValueAccessor, Validator, IThreedExpandable {

  private _isDisabled: boolean;

  @Input()
  set isDisabled(value: boolean) {
  if (this._isDisabled !== value) {
    this._isDisabled = value;
    if (this.threedEnvironmentSettingsFormGroup) {
      this.setDisabledState(value);
      }
  }
  }

  get isDisabled(): boolean {
    return this._isDisabled;
  }

  @Input()
  aliasController: IAliasController;

  @Input()
  sceneEditor: ThreedGenericSceneManager;

  @ViewChild("entityKeySettings")
  entityKeySettings?: ThreedEntityKeySettingsComponent;

  @ViewChild("threedObjectSettings") threedObjectSettings?: ThreedObjectSettingsComponent;

  private modelValue: ThreedEnvironmentSettings;

  private propagateChange = null;

  public threedEnvironmentSettingsFormGroup: FormGroup;

  public entityAttribute?: string;
  private lastEntityKeySettings?: ThreedEntityKeySettings;

  ENVIRONMENT_ID = ENVIRONMENT_ID;

  constructor(protected store: Store<AppState>,
    private translate: TranslateService,
    private fb: FormBuilder) {
    super(store);

  }

  ngOnInit(): void {
    this.threedEnvironmentSettingsFormGroup = this.fb.group({
      threedEntityAliasSettings: [null, []],
      threedEntityKeySettings: [null, []],
      useAlias: [false, []],
      objectSettings: [null, []]
    });
    
    this.threedEnvironmentSettingsFormGroup.get('useAlias').valueChanges.subscribe(() => this.updateValidators(true));
    this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').valueChanges.subscribe(() => this.updateEntityKeySettings());
    this.threedEnvironmentSettingsFormGroup.get('threedEntityAliasSettings').valueChanges.subscribe(() => this.loadEntity());
    this.threedEnvironmentSettingsFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
    this.setDisabledState(this.isDisabled || false);
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }
  registerOnTouched(fn: any): void {
  }

  validate(control: AbstractControl): ValidationErrors {
    return this.threedEnvironmentSettingsFormGroup.valid ? null : {
      threedEnvironmentSettings: {
        valid: false,
      },
    };
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  
    if (isDisabled) {
      this.threedEnvironmentSettingsFormGroup.disable({ emitEvent: false });
    } else {
      this.threedEnvironmentSettingsFormGroup.enable({ emitEvent: false });
  
      const useAlias: boolean = this.threedEnvironmentSettingsFormGroup.get('useAlias').value;
  
      if (!useAlias) {
        this.threedEnvironmentSettingsFormGroup.get('threedEntityAliasSettings').disable({ emitEvent: false });
        this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').disable({ emitEvent: false });
      }
    }
  }
  

  writeValue(value: ThreedEnvironmentSettings): void {
    this.modelValue = value;

    this.threedEnvironmentSettingsFormGroup.patchValue(
      this.modelValue, { emitEvent: false }
    );
    this.lastEntityKeySettings = this.modelValue?.threedEntityKeySettings;
    
    this.updateValidators(false);
  }

  private updateModel() {
    const value: ThreedEnvironmentSettings = this.threedEnvironmentSettingsFormGroup.value;
    this.modelValue = value;

    this.entityKeySettings?.updateEntityAlias(value.threedEntityAliasSettings?.entityAlias);

    if (!this.propagateChange) {
      return;
    }

    if (this.threedEnvironmentSettingsFormGroup.valid) {
      this.propagateChange(this.modelValue);
    } else {
      this.propagateChange(null);
    }
  }

  private updateValidators(emitEvent: boolean) {
    const useAlias: boolean = this.threedEnvironmentSettingsFormGroup.get('useAlias').value;

    if (useAlias) {
      this.threedEnvironmentSettingsFormGroup.get('threedEntityAliasSettings').enable({ emitEvent });
      this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').enable({ emitEvent });
      this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').setValue({ entityAttribute: this.lastEntityKeySettings?.entityAttribute || "" }, { emitEvent });
      this.entityAttribute = this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').value.entityAttribute;
    } else {
      this.lastEntityKeySettings = this.modelValue?.threedEntityKeySettings;
      this.threedEnvironmentSettingsFormGroup.get('threedEntityAliasSettings').disable({ emitEvent });
      this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').disable({ emitEvent });
      this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').setValue({ entityAttribute: null }, { emitEvent });
      this.entityAttribute = null;
    }
    this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').updateValueAndValidity({ emitEvent });
  }

  private updateEntityKeySettings() {
    const useAttribute = this.threedEnvironmentSettingsFormGroup.get('useAlias').value;
    const entityAttribute = this.threedEnvironmentSettingsFormGroup.get('threedEntityKeySettings').value.entityAttribute;
    this.entityAttribute = useAttribute ? entityAttribute || "" : null;
  }

  onEntityAliasChanged() {
    this.entityKeySettings?.invalidateInput();
  }

  private loadEntity() {
    const threedEntityAliasSettings: ThreedEntityAliasSettings = this.threedEnvironmentSettingsFormGroup.get('threedEntityAliasSettings').value;
    if (!threedEntityAliasSettings) return;

    const entityAliasId = this.aliasController.getEntityAliasId(threedEntityAliasSettings.entityAlias);
    if (entityAliasId == null) {
      this.threedEnvironmentSettingsFormGroup.get('objectSettings').patchValue({ entity: null });
      return;
    }

    this.aliasController.resolveSingleEntityInfo(entityAliasId).subscribe(entity => {
      this.threedEnvironmentSettingsFormGroup.get('objectSettings').patchValue({ entity });
    });
  }

  public forceExpand(id: string) {
    if(id == this.modelValue.objectSettings.entity?.id || id == ENVIRONMENT_ID) {
      this.threedObjectSettings.expanded = true;      
    }
  }
}