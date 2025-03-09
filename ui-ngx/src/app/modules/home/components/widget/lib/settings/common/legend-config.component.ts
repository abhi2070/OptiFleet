

import { Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { isDefined } from '@core/utils';
import {
  LegendConfig,
  LegendDirection,
  legendDirectionTranslationMap,
  LegendPosition,
  legendPositionTranslationMap
} from '@shared/models/widget.models';
import { Subscription } from 'rxjs';

// @dynamic
@Component({
  selector: 'tb-legend-config',
  templateUrl: './legend-config.component.html',
  styleUrls: ['./../widget-settings.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => LegendConfigComponent),
      multi: true
    }
  ]
})
export class LegendConfigComponent implements OnInit, OnDestroy, ControlValueAccessor {

  @Input() disabled: boolean;

  legendConfigForm: UntypedFormGroup;
  legendDirection = LegendDirection;
  legendDirections = Object.keys(LegendDirection);
  legendDirectionTranslations = legendDirectionTranslationMap;
  legendPosition = LegendPosition;
  legendPositions = Object.keys(LegendPosition);
  legendPositionTranslations = legendPositionTranslationMap;

  private legendSettingsFormChanges$: Subscription;
  private legendSettingsFormDirectionChanges$: Subscription;
  private propagateChange = (_: any) => {};

  constructor(private fb: UntypedFormBuilder) {
  }

  ngOnInit(): void {
    this.legendConfigForm = this.fb.group({
      direction: [null, []],
      position: [null, []],
      showValues: [[], []],
      sortDataKeys: [null, []]
    });
    this.legendSettingsFormDirectionChanges$ = this.legendConfigForm.get('direction').valueChanges
      .subscribe((direction: LegendDirection) => {
        this.onDirectionChanged(direction);
      });
    this.legendSettingsFormChanges$ = this.legendConfigForm.valueChanges.subscribe(
      () => this.legendConfigUpdated()
    );
  }

  private onDirectionChanged(direction: LegendDirection) {
    if (direction === LegendDirection.row) {
      let position: LegendPosition = this.legendConfigForm.get('position').value;
      if (position !== LegendPosition.bottom && position !== LegendPosition.top) {
        position = LegendPosition.bottom;
      }
      this.legendConfigForm.patchValue({position}, {emitEvent: false}
      );
    }
  }

  ngOnDestroy(): void {
    if (this.legendSettingsFormDirectionChanges$) {
      this.legendSettingsFormDirectionChanges$.unsubscribe();
      this.legendSettingsFormDirectionChanges$ = null;
    }
    if (this.legendSettingsFormChanges$) {
      this.legendSettingsFormChanges$.unsubscribe();
      this.legendSettingsFormChanges$ = null;
    }
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.legendConfigForm.disable({emitEvent: false});
    } else {
      this.legendConfigForm.enable({emitEvent: false});
    }
  }

  writeValue(legendConfig: LegendConfig): void {
    if (legendConfig) {
      this.legendConfigForm.patchValue({
        direction: legendConfig.direction,
        position: legendConfig.position,
        showValues: this.getShowValues(legendConfig),
        sortDataKeys: isDefined(legendConfig.sortDataKeys) ? legendConfig.sortDataKeys : false
      }, {emitEvent: false});
    }
    this.onDirectionChanged(legendConfig.direction);
  }

  private legendConfigUpdated() {
    const configValue = this.legendConfigForm.value;
    const legendConfig: Partial<LegendConfig> = {
      direction: configValue.direction,
      position: configValue.position,
      sortDataKeys: configValue.sortDataKeys
    };
    this.setShowValues(configValue.showValues, legendConfig);
    this.propagateChange(legendConfig);
  }

  private getShowValues(legendConfig: LegendConfig): string[] {
    const showValues: string[] = [];
    if (isDefined(legendConfig.showMin) && legendConfig.showMin) {
      showValues.push('min');
    }
    if (isDefined(legendConfig.showMax) && legendConfig.showMax) {
      showValues.push('max');
    }
    if (isDefined(legendConfig.showAvg) && legendConfig.showAvg) {
      showValues.push('average');
    }
    if (isDefined(legendConfig.showTotal) && legendConfig.showTotal) {
      showValues.push('total');
    }
    if (isDefined(legendConfig.showLatest) && legendConfig.showLatest) {
      showValues.push('latest');
    }
    return showValues;
  }

  private setShowValues(showValues: string[], legendConfig: Partial<LegendConfig>) {
    legendConfig.showMin = showValues.includes('min');
    legendConfig.showMax = showValues.includes('max');
    legendConfig.showAvg = showValues.includes('average');
    legendConfig.showTotal = showValues.includes('total');
    legendConfig.showLatest = showValues.includes('latest');
  }
}
