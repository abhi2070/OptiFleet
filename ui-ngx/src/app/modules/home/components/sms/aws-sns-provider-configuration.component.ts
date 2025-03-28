

import { Component, forwardRef, Input, OnInit } from '@angular/core';
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { isDefinedAndNotNull } from '@core/utils';
import {
  AwsSnsSmsProviderConfiguration,
  SmsProviderConfiguration,
  SmsProviderType
} from '@shared/models/settings.models';

@Component({
  selector: 'tb-aws-sns-provider-configuration',
  templateUrl: './aws-sns-provider-configuration.component.html',
  styleUrls: [],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => AwsSnsProviderConfigurationComponent),
    multi: true
  }]
})
export class AwsSnsProviderConfigurationComponent implements ControlValueAccessor, OnInit {

  awsSnsProviderConfigurationFormGroup: UntypedFormGroup;

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
    this.awsSnsProviderConfigurationFormGroup = this.fb.group({
        accessKeyId: [null, [Validators.required]],
        secretAccessKey: [null, [Validators.required]],
        region: [null, [Validators.required]]
    });
    this.awsSnsProviderConfigurationFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.awsSnsProviderConfigurationFormGroup.disable({emitEvent: false});
    } else {
      this.awsSnsProviderConfigurationFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: AwsSnsSmsProviderConfiguration | null): void {
    if (isDefinedAndNotNull(value)) {
      this.awsSnsProviderConfigurationFormGroup.patchValue(value, {emitEvent: false});
    }
  }

  private updateModel() {
    let configuration: AwsSnsSmsProviderConfiguration = null;
    if (this.awsSnsProviderConfigurationFormGroup.valid) {
      configuration = this.awsSnsProviderConfigurationFormGroup.value;
      (configuration as SmsProviderConfiguration).type = SmsProviderType.AWS_SNS;
    }
    this.propagateChange(configuration);
  }
}
