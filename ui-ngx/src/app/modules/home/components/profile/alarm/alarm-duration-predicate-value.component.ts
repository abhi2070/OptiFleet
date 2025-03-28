

import { Component, forwardRef, Input, OnInit } from '@angular/core';
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import {
  DynamicValueSourceType,
  dynamicValueSourceTypeTranslationMap,
  FilterPredicateValue,
  getDynamicSourcesForAllowUser,
  inheritModeForDynamicValueSourceType
} from '@shared/models/query/query.models';
import { AlarmConditionType } from '@shared/models/device.models';

@Component({
  selector: 'tb-alarm-duration-predicate-value',
  templateUrl: './alarm-duration-predicate-value.component.html',
  styleUrls: [],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AlarmDurationPredicateValueComponent),
      multi: true
    }
  ]
})
export class AlarmDurationPredicateValueComponent implements ControlValueAccessor, OnInit {

  private readonly inheritModeForSources = inheritModeForDynamicValueSourceType;

  @Input()
  set alarmConditionType(alarmConditionType: AlarmConditionType) {
    switch (alarmConditionType) {
      case AlarmConditionType.REPEATING:
        this.defaultValuePlaceholder = 'device-profile.condition-repeating-value-required';
        this.defaultValueRequiredError = 'device-profile.condition-repeating-value-range';
        this.defaultValueRangeError = 'device-profile.condition-repeating-value-range';
        this.defaultValuePatternError = 'device-profile.condition-repeating-value-pattern';
        break;
      case AlarmConditionType.DURATION:
        this.defaultValuePlaceholder = 'device-profile.condition-duration-value';
        this.defaultValueRequiredError = 'device-profile.condition-duration-value-required';
        this.defaultValueRangeError = 'device-profile.condition-duration-value-range';
        this.defaultValuePatternError = 'device-profile.condition-duration-value-pattern';
        break;
    }
  }

  defaultValuePlaceholder = '';
  defaultValueRequiredError = '';
  defaultValueRangeError = '';
  defaultValuePatternError = '';

  dynamicValueSourceTypes: DynamicValueSourceType[] = getDynamicSourcesForAllowUser(false);

  dynamicValueSourceTypeTranslations = dynamicValueSourceTypeTranslationMap;

  alarmDurationPredicateValueFormGroup: UntypedFormGroup;

  dynamicMode = false;

  inheritMode = false;

  private propagateChange = null;

  constructor(private fb: UntypedFormBuilder) {
  }

  ngOnInit(): void {
    this.alarmDurationPredicateValueFormGroup = this.fb.group({
      defaultValue: [0, [Validators.required, Validators.min(1), Validators.max(2147483647), Validators.pattern('[0-9]*')]],
      dynamicValue: this.fb.group(
        {
          sourceType: [null],
          sourceAttribute: [null],
          inherit: [false]
        }
      )
    });
    this.alarmDurationPredicateValueFormGroup.get('dynamicValue').get('sourceType').valueChanges.subscribe(
      (sourceType) => {
        if (!sourceType) {
          this.alarmDurationPredicateValueFormGroup.get('dynamicValue').get('sourceAttribute').patchValue(null, {emitEvent: false});
        }
        this.updateShowInheritMode(sourceType);
      }
    );
    this.alarmDurationPredicateValueFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.alarmDurationPredicateValueFormGroup.disable({emitEvent: false});
    } else {
      this.alarmDurationPredicateValueFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(predicateValue: FilterPredicateValue<string | number | boolean>): void {
    this.alarmDurationPredicateValueFormGroup.patchValue({
      defaultValue: predicateValue ? predicateValue.defaultValue : null,
      dynamicValue: {
        sourceType: predicateValue?.dynamicValue ? predicateValue.dynamicValue.sourceType : null,
        sourceAttribute: predicateValue?.dynamicValue ? predicateValue.dynamicValue.sourceAttribute : null,
        inherit: predicateValue?.dynamicValue ? predicateValue.dynamicValue.inherit : null
      }
    }, {emitEvent: false});

    this.updateShowInheritMode(this.alarmDurationPredicateValueFormGroup.get('dynamicValue').get('sourceType').value);
  }

  private updateModel() {
    let predicateValue: FilterPredicateValue<string | number | boolean> = null;
    if (this.alarmDurationPredicateValueFormGroup.valid) {
      predicateValue = this.alarmDurationPredicateValueFormGroup.getRawValue();
      if (predicateValue.dynamicValue) {
        if (!predicateValue.dynamicValue.sourceType || !predicateValue.dynamicValue.sourceAttribute) {
          predicateValue.dynamicValue = null;
        }
      }
    }
    this.propagateChange(predicateValue);
  }

  private updateShowInheritMode(sourceType: DynamicValueSourceType) {
    if (this.inheritModeForSources.includes(sourceType)) {
      this.inheritMode = true;
    } else {
      this.alarmDurationPredicateValueFormGroup.get('dynamicValue.inherit').patchValue(false, {emitEvent: false});
      this.inheritMode = false;
    }
  }
}
