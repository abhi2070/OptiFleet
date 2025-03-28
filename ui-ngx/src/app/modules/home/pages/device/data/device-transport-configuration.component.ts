

import { Component, forwardRef, Input, OnInit } from '@angular/core';
import {
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { DeviceTransportConfiguration, DeviceTransportType } from '@shared/models/device.models';
import { deepClone } from '@core/utils';

@Component({
  selector: 'tb-device-transport-configuration',
  templateUrl: './device-transport-configuration.component.html',
  styleUrls: [],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DeviceTransportConfigurationComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => DeviceTransportConfigurationComponent),
      multi: true
    }]
})
export class DeviceTransportConfigurationComponent implements ControlValueAccessor, OnInit, Validator {

  deviceTransportType = DeviceTransportType;

  deviceTransportConfigurationFormGroup: UntypedFormGroup;

  private requiredValue: boolean;
  get required(): boolean {
    return this.requiredValue;
  }
  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

  @Input()
  disabled: boolean;

  transportType: DeviceTransportType;

  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
              private fb: UntypedFormBuilder) {
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  ngOnInit() {
    this.deviceTransportConfigurationFormGroup = this.fb.group({
      configuration: [null, Validators.required]
    });
    this.deviceTransportConfigurationFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.deviceTransportConfigurationFormGroup.disable({emitEvent: false});
    } else {
      this.deviceTransportConfigurationFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: DeviceTransportConfiguration | null): void {
    this.transportType = value?.type;
    const configuration = deepClone(value);
    if (configuration) {
      delete configuration.type;
    }
    this.deviceTransportConfigurationFormGroup.patchValue({configuration}, {emitEvent: false});
  }

  validate(): ValidationErrors | null {
    return this.deviceTransportConfigurationFormGroup.valid ? null : {
      deviceTransportConfiguration: false
    };
  }

  private updateModel() {
    let configuration: DeviceTransportConfiguration = null;
    if (this.deviceTransportConfigurationFormGroup.valid) {
      configuration = this.deviceTransportConfigurationFormGroup.getRawValue().configuration;
      configuration.type = this.transportType;
    }
    this.propagateChange(configuration);
  }
}
