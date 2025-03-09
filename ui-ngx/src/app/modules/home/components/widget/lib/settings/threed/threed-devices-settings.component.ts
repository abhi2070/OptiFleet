import { Component, Input, OnInit, forwardRef, ChangeDetectorRef } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  FormArray,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { IAliasController } from '@app/core/public-api';
import { AppState } from '@core/core.state';
import { ThreedDeviceGroupSettings, ThreedDevicesSettings, defaultThreedDeviceGroupSettings } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { PageComponent } from '@shared/components/page.component';
import { ThreedGenericSceneManager } from '../../../threed-view-widget/threed/threed-managers/threed-generic-scene-manager';
import { ThreedTransformControllerComponent } from '../../../threed-view-widget/threed/threed-components/threed-transform-controller-component';
import { IThreedExpandable } from './ithreed-expandable';

@Component({
  selector: 'tb-threed-devices-settings',
  templateUrl: './threed-devices-settings.component.html',
  styleUrls: ['./../widget-settings.scss', './threed-devices-settings.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ThreedDevicesSettingsComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ThreedDevicesSettingsComponent),
      multi: true
    }
  ]
})

export class ThreedDevicesSettingsComponent extends PageComponent implements OnInit, ControlValueAccessor, Validator, IThreedExpandable {


  @Input()
  aliasController: IAliasController;

  @Input()
  sceneEditor: ThreedGenericSceneManager;

  @Input()
  disabled: boolean;

  private modelValue: ThreedDevicesSettings;

  private propagateChange = null;

  public threedDevicesSettingsFormGroup: FormGroup;

  constructor(protected store: Store<AppState>,
    private translate: TranslateService,
    private fb: FormBuilder,
    private cd: ChangeDetectorRef) {
    super(store);
  }

  ngOnInit(): void {
    this.threedDevicesSettingsFormGroup = this.fb.group({
      threedDeviceGroupSettings: this.prepareDeviceGroupsFormArray([]),
    });
    this.threedDevicesSettingsFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });

  }


  private prepareDeviceGroupsFormArray(threedDeviceGroupSettings: ThreedDeviceGroupSettings[] | undefined): FormArray {
    const deviceGroupsControls: Array<AbstractControl> = [];
    if (threedDeviceGroupSettings) {
      threedDeviceGroupSettings.forEach((deviceGroup) => {
        deviceGroupsControls.push(this.fb.control(deviceGroup, [Validators.required]));
      });
    }
    return this.fb.array(deviceGroupsControls);
  }

  deviceGroupsFormArray(): FormArray {
    return this.threedDevicesSettingsFormGroup.get('threedDeviceGroupSettings') as FormArray;
  }

  public trackByDeviceGroupControl(index: number, deviceGroupControl: AbstractControl): any {
    return deviceGroupControl;
  }


  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  validate(control: AbstractControl): ValidationErrors {
    return this.threedDevicesSettingsFormGroup.valid ? null : {
      threedDevicesSettings: {
        valid: false,
      },
    };
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.threedDevicesSettingsFormGroup.disable({ emitEvent: false });
    } else {
      this.threedDevicesSettingsFormGroup.enable({ emitEvent: false });
    }
  }

  writeValue(value: ThreedDevicesSettings): void {
    this.modelValue = value;

    this.threedDevicesSettingsFormGroup.setControl('threedDeviceGroupSettings', this.prepareDeviceGroupsFormArray(value.threedDeviceGroupSettings), { emitEvent: false });
  }

  private updateModel() {
    const value: ThreedDevicesSettings = this.threedDevicesSettingsFormGroup.value;
    this.modelValue = value;

    if (!this.propagateChange) {
      return;
    }

    if (this.threedDevicesSettingsFormGroup.valid) {
      this.propagateChange(this.modelValue);
    } else {
      this.propagateChange(null);
    }
  }

  public addDeviceGroup() {
    const deviceGroup: ThreedDeviceGroupSettings = defaultThreedDeviceGroupSettings;
    const deviceGroupsArray = this.threedDevicesSettingsFormGroup.get('threedDeviceGroupSettings') as FormArray;
    const deviceGroupControl = this.fb.control(deviceGroup, [Validators.required]);
    (deviceGroupControl as any).new = true;
    deviceGroupsArray.push(deviceGroupControl);
    this.threedDevicesSettingsFormGroup.updateValueAndValidity();
  }

  public removeDeviceGroup(index: number) {
    (this.threedDevicesSettingsFormGroup.get('threedDeviceGroupSettings') as FormArray).removeAt(index);
  }

  public forceExpand(id: string): void {
    const devicesGoupFormArray = this.deviceGroupsFormArray();

    for (let i = 0; i < devicesGoupFormArray.controls.length; i++) {
      const control = devicesGoupFormArray.controls[i];
      const deviceGroup: ThreedDeviceGroupSettings = devicesGoupFormArray.value[i];
      if (deviceGroup.threedObjectSettings.find(o => o.entity.id == id)) {
        // @ts-ignore
        control.expandedId = id;
        this.cd.detectChanges();
        return;
      }
    }
  }
}
