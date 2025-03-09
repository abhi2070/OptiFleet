

import { Component, Injector } from '@angular/core';
import {
  legendPositions,
  legendPositionTranslationMap,
  WidgetSettings,
  WidgetSettingsComponent
} from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { formatValue } from '@core/utils';
import { rangeChartDefaultSettings } from '@home/components/widget/lib/chart/range-chart-widget.models';
import { DateFormatProcessor, DateFormatSettings } from '@shared/models/widget-settings.models';

@Component({
  selector: 'tb-range-chart-widget-settings',
  templateUrl: './range-chart-widget-settings.component.html',
  styleUrls: []
})
export class RangeChartWidgetSettingsComponent extends WidgetSettingsComponent {

  legendPositions = legendPositions;

  legendPositionTranslationMap = legendPositionTranslationMap;

  rangeChartWidgetSettingsForm: UntypedFormGroup;

  tooltipValuePreviewFn = this._tooltipValuePreviewFn.bind(this);

  tooltipDatePreviewFn = this._tooltipDatePreviewFn.bind(this);

  constructor(protected store: Store<AppState>,
              private $injector: Injector,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  protected settingsForm(): UntypedFormGroup {
    return this.rangeChartWidgetSettingsForm;
  }

  protected defaultSettings(): WidgetSettings {
    return {...rangeChartDefaultSettings};
  }

  protected onSettingsSet(settings: WidgetSettings) {
    this.rangeChartWidgetSettingsForm = this.fb.group({
      dataZoom: [settings.dataZoom, []],
      rangeColors: [settings.rangeColors, []],
      outOfRangeColor: [settings.outOfRangeColor, []],
      fillArea: [settings.fillArea, []],

      showLegend: [settings.showLegend, []],
      legendPosition: [settings.legendPosition, []],
      legendLabelFont: [settings.legendLabelFont, []],
      legendLabelColor: [settings.legendLabelColor, []],

      showTooltip: [settings.showTooltip, []],
      tooltipValueFont: [settings.tooltipValueFont, []],
      tooltipValueColor: [settings.tooltipValueColor, []],
      tooltipShowDate: [settings.tooltipShowDate, []],
      tooltipDateFormat: [settings.tooltipDateFormat, []],
      tooltipDateFont: [settings.tooltipDateFont, []],
      tooltipDateColor: [settings.tooltipDateColor, []],
      tooltipBackgroundColor: [settings.tooltipBackgroundColor, []],
      tooltipBackgroundBlur: [settings.tooltipBackgroundBlur, []],

      background: [settings.background, []]
    });
  }

  protected validatorTriggers(): string[] {
    return ['showLegend', 'showTooltip', 'tooltipShowDate'];
  }

  protected updateValidators(emitEvent: boolean) {
    const showLegend: boolean = this.rangeChartWidgetSettingsForm.get('showLegend').value;
    const showTooltip: boolean = this.rangeChartWidgetSettingsForm.get('showTooltip').value;
    const tooltipShowDate: boolean = this.rangeChartWidgetSettingsForm.get('tooltipShowDate').value;

    if (showLegend) {
      this.rangeChartWidgetSettingsForm.get('legendPosition').enable();
      this.rangeChartWidgetSettingsForm.get('legendLabelFont').enable();
      this.rangeChartWidgetSettingsForm.get('legendLabelColor').enable();
    } else {
      this.rangeChartWidgetSettingsForm.get('legendPosition').disable();
      this.rangeChartWidgetSettingsForm.get('legendLabelFont').disable();
      this.rangeChartWidgetSettingsForm.get('legendLabelColor').disable();
    }

    if (showTooltip) {
      this.rangeChartWidgetSettingsForm.get('tooltipValueFont').enable();
      this.rangeChartWidgetSettingsForm.get('tooltipValueColor').enable();
      this.rangeChartWidgetSettingsForm.get('tooltipShowDate').enable({emitEvent: false});
      this.rangeChartWidgetSettingsForm.get('tooltipBackgroundColor').enable();
      this.rangeChartWidgetSettingsForm.get('tooltipBackgroundBlur').enable();
      if (tooltipShowDate) {
        this.rangeChartWidgetSettingsForm.get('tooltipDateFormat').enable();
        this.rangeChartWidgetSettingsForm.get('tooltipDateFont').enable();
        this.rangeChartWidgetSettingsForm.get('tooltipDateColor').enable();
      } else {
        this.rangeChartWidgetSettingsForm.get('tooltipDateFormat').disable();
        this.rangeChartWidgetSettingsForm.get('tooltipDateFont').disable();
        this.rangeChartWidgetSettingsForm.get('tooltipDateColor').disable();
      }
    } else {
      this.rangeChartWidgetSettingsForm.get('tooltipValueFont').disable();
      this.rangeChartWidgetSettingsForm.get('tooltipValueColor').disable();
      this.rangeChartWidgetSettingsForm.get('tooltipShowDate').disable({emitEvent: false});
      this.rangeChartWidgetSettingsForm.get('tooltipDateFormat').disable();
      this.rangeChartWidgetSettingsForm.get('tooltipDateFont').disable();
      this.rangeChartWidgetSettingsForm.get('tooltipDateColor').disable();
      this.rangeChartWidgetSettingsForm.get('tooltipBackgroundColor').disable();
      this.rangeChartWidgetSettingsForm.get('tooltipBackgroundBlur').disable();
    }
  }

  private _tooltipValuePreviewFn(): string {
    const units: string = this.widgetConfig.config.units;
    const decimals: number = this.widgetConfig.config.decimals;
    return formatValue(22, decimals, units, false);
  }

  private _tooltipDatePreviewFn(): string {
    const dateFormat: DateFormatSettings = this.rangeChartWidgetSettingsForm.get('tooltipDateFormat').value;
    const processor = DateFormatProcessor.fromSettings(this.$injector, dateFormat);
    processor.update(Date.now());
    return processor.formatted;
  }

}
