

import { Component, EventEmitter, forwardRef, Input, OnInit, Output } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormGroup,
  NG_VALUE_ACCESSOR, ValidatorFn,
  Validators
} from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { isNumber } from '@core/utils';

export interface GpioItem {
  pin: number;
  label: string;
  row: number;
  col: number;
  color?: string;
}

export const gpioItemValidator = (hasColor: boolean): ValidatorFn => (control: AbstractControl) => {
  const gpioItem: GpioItem = control.value;
  if (!gpioItem
    || !isNumber(gpioItem.pin) || gpioItem.pin < 1
    || !isNumber(gpioItem.row) || gpioItem.row < 0
    || !isNumber(gpioItem.col) || gpioItem.col < 0 || gpioItem.col > 1
    || !gpioItem.label
    || (hasColor && !gpioItem.color)
  ) {
    return {
      gpioItem: true
    };
  }
  return null;
};

@Component({
  selector: 'tb-gpio-item',
  templateUrl: './gpio-item.component.html',
  styleUrls: ['./gpio-item.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => GpioItemComponent),
      multi: true
    }
  ]
})
export class GpioItemComponent extends PageComponent implements OnInit, ControlValueAccessor {

  @Input()
  disabled: boolean;

  @Input()
  expanded = false;

  @Input()
  hasColor = false;

  @Output()
  removeGpioItem = new EventEmitter();

  private modelValue: GpioItem;

  private propagateChange = null;

  public gpioItemFormGroup: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private translate: TranslateService,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  ngOnInit(): void {
    this.gpioItemFormGroup = this.fb.group({
      pin: [null, [Validators.required, Validators.min(1)]],
      label: [null, [Validators.required]],
      row: [null, [Validators.required, Validators.min(0)]],
      col: [null, [Validators.required, Validators.min(0), Validators.max(1)]]
    });
    if (this.hasColor) {
      this.gpioItemFormGroup.addControl('color', this.fb.control(null, [Validators.required]));
    }
    this.gpioItemFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.gpioItemFormGroup.disable({emitEvent: false});
    } else {
      this.gpioItemFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: GpioItem): void {
    this.modelValue = value;
    this.gpioItemFormGroup.patchValue(
      value, {emitEvent: false}
    );
  }

  numberText(value: any, minValue: number): string {
    return isNumber(value) && value > minValue ? value : 'Undefined';
  }

  private updateModel() {
    const value: GpioItem = this.gpioItemFormGroup.value;
    this.modelValue = value;
    this.propagateChange(this.modelValue);
  }
}
