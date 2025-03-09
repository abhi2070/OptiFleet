

import { Component, forwardRef, HostBinding, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { _ToggleBase, ToggleHeaderAppearance } from '@shared/components/toggle-header.component';
import { coerceBoolean } from '@shared/decorators/coercion';

@Component({
  selector: 'tb-toggle-select',
  templateUrl: './toggle-select.component.html',
  styleUrls: [],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ToggleSelectComponent),
      multi: true
    }
  ]
})
export class ToggleSelectComponent extends _ToggleBase implements ControlValueAccessor {

  @HostBinding('style.maxWidth')
  get maxWidth() { return '100%'; }

  @Input()
  @coerceBoolean()
  disabled: boolean;

  @Input()
  selectMediaBreakpoint;

  @Input()
  appearance: ToggleHeaderAppearance = 'stroked';

  @Input()
  @coerceBoolean()
  disablePagination = false;

  modelValue: any;

  private propagateChange = null;

  constructor(protected store: Store<AppState>) {
    super(store);
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(value: any): void {
    this.modelValue = value;
  }

  updateModel(value: any) {
    this.modelValue = value;
    this.propagateChange(this.modelValue);
  }
}
