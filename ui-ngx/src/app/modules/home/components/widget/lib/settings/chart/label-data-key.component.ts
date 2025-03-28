/* eslint-disable prefer-arrow/prefer-arrow-functions */


import { Component, EventEmitter, forwardRef, Input, OnInit, Output } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormGroup,
  NG_VALUE_ACCESSOR, ValidationErrors,
  Validators
} from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { DataKeyType } from '@shared/models/telemetry/telemetry.models';

export interface LabelDataKey {
  name: string;
  type: DataKeyType;
}

export function labelDataKeyValidator(control: AbstractControl): ValidationErrors | null {
  const labelDataKey: LabelDataKey = control.value;
  if (!labelDataKey || !labelDataKey.name) {
    return {
      labelDataKey: true
    };
  }
  return null;
}

@Component({
  selector: 'tb-label-data-key',
  templateUrl: './label-data-key.component.html',
  styleUrls: ['./label-data-key.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => LabelDataKeyComponent),
      multi: true
    }
  ]
})
export class LabelDataKeyComponent extends PageComponent implements OnInit, ControlValueAccessor {

  @Input()
  disabled: boolean;

  @Input()
  expanded = false;

  @Output()
  removeLabelDataKey = new EventEmitter();

  private modelValue: LabelDataKey;

  private propagateChange = null;

  public labelDataKeyFormGroup: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private translate: TranslateService,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  ngOnInit(): void {
    this.labelDataKeyFormGroup = this.fb.group({
      name: [null, [Validators.required]],
      type: [DataKeyType.attribute, [Validators.required]]
    });
    this.labelDataKeyFormGroup.valueChanges.subscribe(() => {
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
      this.labelDataKeyFormGroup.disable({emitEvent: false});
    } else {
      this.labelDataKeyFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: LabelDataKey): void {
    this.modelValue = value;
    this.labelDataKeyFormGroup.patchValue(
      value, {emitEvent: false}
    );
  }

  labelDataKeyText(): string {
    const name: string = this.labelDataKeyFormGroup.get('name').value || '';
    const type: DataKeyType = this.labelDataKeyFormGroup.get('type').value;
    let typeStr: string;
    if (type === DataKeyType.attribute) {
      typeStr = this.translate.instant('widgets.chart.key-type-attribute');
    } else if (type === DataKeyType.timeseries) {
      typeStr = this.translate.instant('widgets.chart.key-type-timeseries');
    }
    return `${name} (${typeStr})`;
  }

  private updateModel() {
    const value: LabelDataKey = this.labelDataKeyFormGroup.value;
    this.modelValue = value;
    this.propagateChange(this.modelValue);
  }
}
