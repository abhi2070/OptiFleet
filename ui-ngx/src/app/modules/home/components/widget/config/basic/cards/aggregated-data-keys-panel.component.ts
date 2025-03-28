

import {
  ChangeDetectorRef,
  Component,
  forwardRef,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewEncapsulation
} from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALUE_ACCESSOR,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { WidgetConfigComponent } from '@home/components/widget/widget-config.component';
import { DataKey, DatasourceType, widgetType } from '@shared/models/widget.models';
import { DataKeyType } from '@shared/models/telemetry/telemetry.models';
import { UtilsService } from '@core/services/utils.service';
import { DataKeysCallbacks } from '@home/components/widget/config/data-keys.component.models';
import { aggregatedValueCardDefaultKeySettings } from '@home/components/widget/lib/cards/aggregated-value-card.models';

@Component({
  selector: 'tb-aggregated-data-keys-panel',
  templateUrl: './aggregated-data-keys-panel.component.html',
  styleUrls: ['./aggregated-data-keys-panel.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AggregatedDataKeysPanelComponent),
      multi: true
    }
  ],
  encapsulation: ViewEncapsulation.None
})
export class AggregatedDataKeysPanelComponent implements ControlValueAccessor, OnInit, OnChanges {

  @Input()
  disabled: boolean;

  @Input()
  datasourceType: DatasourceType;

  @Input()
  keyName: string;

  dataKeyType: DataKeyType;

  keysListFormGroup: UntypedFormGroup;

  get widgetType(): widgetType {
    return this.widgetConfigComponent.widgetType;
  }

  get callbacks(): DataKeysCallbacks {
    return this.widgetConfigComponent.widgetConfigCallbacks;
  }

  get noKeys(): boolean {
    const keys: DataKey[] = this.keysListFormGroup.get('keys').value;
    return keys.length === 0;
  }

  private propagateChange = (_val: any) => {};

  constructor(private fb: UntypedFormBuilder,
              private dialog: MatDialog,
              private cd: ChangeDetectorRef,
              private utils: UtilsService,
              private widgetConfigComponent: WidgetConfigComponent) {
  }

  ngOnInit() {
    this.keysListFormGroup = this.fb.group({
      keys: [this.fb.array([]), []]
    });
    this.keysListFormGroup.valueChanges.subscribe(
      (val) => this.propagateChange(this.keysListFormGroup.get('keys').value)
    );
    this.updateParams();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const propName of Object.keys(changes)) {
      const change = changes[propName];
      if (!change.firstChange && change.currentValue !== change.previousValue) {
        if (['datasourceType'].includes(propName)) {
          this.updateParams();
        }
      }
    }
  }

  private updateParams() {
    if (this.datasourceType === DatasourceType.function) {
      this.dataKeyType = DataKeyType.function;
    } else {
      this.dataKeyType = DataKeyType.timeseries;
    }
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.keysListFormGroup.disable({emitEvent: false});
    } else {
      this.keysListFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: DataKey[] | undefined): void {
    this.keysListFormGroup.setControl('keys', this.prepareKeysFormArray(value), {emitEvent: false});
  }

  keysFormArray(): UntypedFormArray {
    return this.keysListFormGroup.get('keys') as UntypedFormArray;
  }

  trackByKey(index: number, keyControl: AbstractControl): any {
    return keyControl;
  }

  removeKey(index: number) {
    (this.keysListFormGroup.get('keys') as UntypedFormArray).removeAt(index);
  }

  addKey() {
    const dataKey = this.callbacks.generateDataKey(this.keyName, this.dataKeyType, null);
    dataKey.decimals = 0;
    dataKey.settings = {...aggregatedValueCardDefaultKeySettings};
    const keysArray = this.keysListFormGroup.get('keys') as UntypedFormArray;
    const keyControl = this.fb.control(dataKey, []);
    keysArray.push(keyControl);
  }

  private prepareKeysFormArray(keys: DataKey[] | undefined): UntypedFormArray {
    const keysControls: Array<AbstractControl> = [];
    if (keys) {
      keys.forEach((key) => {
        keysControls.push(this.fb.control(key, []));
      });
    }
    return this.fb.array(keysControls);
  }

}
