

import { Component, forwardRef, HostBinding, Input, OnInit } from '@angular/core';
import {
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validator,
  Validators
} from '@angular/forms';
import { cssUnit, resolveCssSize } from '@shared/models/widget-settings.models';
import { coerceBoolean } from '@shared/decorators/coercion';
import { isDefinedAndNotNull } from '@core/utils';

@Component({
  selector: 'tb-css-size-input',
  templateUrl: './css-size-input.component.html',
  styleUrls: [],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CssSizeInputComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CssSizeInputComponent),
      multi: true,
    }
  ]
})
export class CssSizeInputComponent implements OnInit, ControlValueAccessor, Validator {

  @HostBinding('style.width')
  get hostWidth(): string {
    return this.flex ? '100%' : null;
  }

  @HostBinding('style.flex')
  get hostFlex(): string {
    return this.flex ? '1' : null;
  }

  @Input()
  disabled: boolean;

  @Input()
  @coerceBoolean()
  required = false;

  @Input()
  requiredText: string;

  @Input()
  @coerceBoolean()
  allowEmptyUnit = false;

  @Input()
  @coerceBoolean()
  flex = false;

  cssSizeFormGroup: UntypedFormGroup;

  modelValue: string;

  private propagateChange = null;

  constructor(private fb: UntypedFormBuilder) {}

  ngOnInit(): void {
    this.cssSizeFormGroup = this.fb.group({
      size: [null, this.required ? [Validators.required, Validators.min(0)] : [Validators.min(0)]],
      unit: [null, []]
    });
    this.cssSizeFormGroup.valueChanges.subscribe((value: {size: number; unit: cssUnit}) => {
      this.updateModel(value);
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.cssSizeFormGroup.disable({emitEvent: false});
    } else {
      this.cssSizeFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: string): void {
    this.modelValue = value;
    const size = resolveCssSize(value);
    this.cssSizeFormGroup.patchValue({
      size: size[0],
      unit: size[1]
    }, {emitEvent: false});
  }

  validate(_c: UntypedFormControl) {
    return this.cssSizeFormGroup.valid ? null : {
      cssSize: {
        valid: false,
      }
    };
  }

  private updateModel(value: {size: number; unit: cssUnit}): void {
    const result: string = isDefinedAndNotNull(value?.size) && isDefinedAndNotNull(value?.unit)
      ? value.size + value.unit : '';
    if (this.modelValue !== result) {
      this.modelValue = result;
      this.propagateChange(this.modelValue);
    }
  }
}
