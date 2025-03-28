

import { Component, forwardRef, Input, OnInit } from '@angular/core';
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
  DefaultDeviceConfiguration,
  DeviceConfiguration,
  DeviceProfileType
} from '@shared/models/device.models';

@Component({
  selector: 'tb-default-device-configuration',
  templateUrl: './default-device-configuration.component.html',
  styleUrls: [],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => DefaultDeviceConfigurationComponent),
    multi: true
  }]
})
export class DefaultDeviceConfigurationComponent implements ControlValueAccessor, OnInit {

  defaultDeviceConfigurationFormGroup: UntypedFormGroup;

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
    this.defaultDeviceConfigurationFormGroup = this.fb.group({
      configuration: [null, Validators.required]
    });
    this.defaultDeviceConfigurationFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.defaultDeviceConfigurationFormGroup.disable({emitEvent: false});
    } else {
      this.defaultDeviceConfigurationFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: DefaultDeviceConfiguration | null): void {
    this.defaultDeviceConfigurationFormGroup.patchValue({configuration: value}, {emitEvent: false});
  }

  private updateModel() {
    let configuration: DeviceConfiguration = null;
    if (this.defaultDeviceConfigurationFormGroup.valid) {
      configuration = this.defaultDeviceConfigurationFormGroup.getRawValue().configuration;
      configuration.type = DeviceProfileType.DEFAULT;
    }
    this.propagateChange(configuration);
  }
}
