

import { NgModule, Type } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { IBasicWidgetConfigComponent } from '@home/components/widget/config/widget-config.component.models';
import { WidgetConfigComponentsModule } from '@home/components/widget/config/widget-config-components.module';
import {
  SimpleCardBasicConfigComponent
} from '@home/components/widget/config/basic/cards/simple-card-basic-config.component';
import {
  WidgetActionsPanelComponent
} from '@home/components/widget/config/basic/common/widget-actions-panel.component';
import {
  EntitiesTableBasicConfigComponent
} from '@home/components/widget/config/basic/entity/entities-table-basic-config.component';
import { DataKeysPanelComponent } from '@home/components/widget/config/basic/common/data-keys-panel.component';
import { DataKeyRowComponent } from '@home/components/widget/config/basic/common/data-key-row.component';
import {
  TimeseriesTableBasicConfigComponent
} from '@home/components/widget/config/basic/cards/timeseries-table-basic-config.component';
import { FlotBasicConfigComponent } from '@home/components/widget/config/basic/chart/flot-basic-config.component';
import {
  AlarmsTableBasicConfigComponent
} from '@home/components/widget/config/basic/alarm/alarms-table-basic-config.component';
import {
  ValueCardBasicConfigComponent
} from '@home/components/widget/config/basic/cards/value-card-basic-config.component';
import {
  AggregatedValueCardBasicConfigComponent
} from '@home/components/widget/config/basic/cards/aggregated-value-card-basic-config.component';
import {
  AggregatedDataKeyRowComponent
} from '@home/components/widget/config/basic/cards/aggregated-data-key-row.component';
import {
  AggregatedDataKeysPanelComponent
} from '@home/components/widget/config/basic/cards/aggregated-data-keys-panel.component';
import {
  AlarmCountBasicConfigComponent
} from '@home/components/widget/config/basic/alarm/alarm-count-basic-config.component';
import {
  EntityCountBasicConfigComponent
} from '@home/components/widget/config/basic/entity/entity-count-basic-config.component';
import {
  BatteryLevelBasicConfigComponent
} from '@home/components/widget/config/basic/indicator/battery-level-basic-config.component';
import {
  WindSpeedDirectionBasicConfigComponent
} from '@home/components/widget/config/basic/weather/wind-speed-direction-basic-config.component';
import {
  SignalStrengthBasicConfigComponent
} from '@home/components/widget/config/basic/indicator/signal-strength-basic-config.component';
import {
  ValueChartCardBasicConfigComponent
} from '@home/components/widget/config/basic/cards/value-chart-card-basic-config.component';
import {
  ProgressBarBasicConfigComponent
} from '@home/components/widget/config/basic/cards/progress-bar-basic-config.component';
import {
  RadialGaugeBasicConfigComponent
} from '@home/components/widget/config/basic/gauge/radial-gauge-basic-config.component';
import {
  ThermometerScaleGaugeBasicConfigComponent
} from '@home/components/widget/config/basic/gauge/thermometer-scale-gauge-basic-config.component';
import {
  CompassGaugeBasicConfigComponent
} from '@home/components/widget/config/basic/gauge/compass-gauge-basic-config.component';
import {
  LiquidLevelCardBasicConfigComponent
} from '@home/components/widget/config/basic/indicator/liquid-level-card-basic-config.component';
import {
  DoughnutBasicConfigComponent
} from '@home/components/widget/config/basic/chart/doughnut-basic-config.component';
import {
  RangeChartBasicConfigComponent
} from '@home/components/widget/config/basic/chart/range-chart-basic-config.component';
import {
  BarChartWithLabelsBasicConfigComponent
} from '@home/components/widget/config/basic/chart/bar-chart-with-labels-basic-config.component';
import {
  SingleSwitchBasicConfigComponent
} from '@home/components/widget/config/basic/rpc/single-switch-basic-config.component';
import {
  ActionButtonBasicConfigComponent
} from '@home/components/widget/config/basic/button/action-button-basic-config.component';
import {
  CommandButtonBasicConfigComponent
} from '@home/components/widget/config/basic/button/command-button-basic-config.component';
import {
  PowerButtonBasicConfigComponent
} from '@home/components/widget/config/basic/button/power-button-basic-config.component';
import { SliderBasicConfigComponent } from '@home/components/widget/config/basic/rpc/slider-basic-config.component';
import {
  ToggleButtonBasicConfigComponent
} from '@home/components/widget/config/basic/button/toggle-button-basic-config.component';

@NgModule({
  declarations: [
    WidgetActionsPanelComponent,
    SimpleCardBasicConfigComponent,
    EntitiesTableBasicConfigComponent,
    TimeseriesTableBasicConfigComponent,
    FlotBasicConfigComponent,
    AlarmsTableBasicConfigComponent,
    ValueCardBasicConfigComponent,
    AggregatedValueCardBasicConfigComponent,
    AggregatedDataKeyRowComponent,
    AggregatedDataKeysPanelComponent,
    DataKeyRowComponent,
    DataKeysPanelComponent,
    AlarmCountBasicConfigComponent,
    EntityCountBasicConfigComponent,
    BatteryLevelBasicConfigComponent,
    WindSpeedDirectionBasicConfigComponent,
    SignalStrengthBasicConfigComponent,
    ValueChartCardBasicConfigComponent,
    ProgressBarBasicConfigComponent,
    RadialGaugeBasicConfigComponent,
    ThermometerScaleGaugeBasicConfigComponent,
    CompassGaugeBasicConfigComponent,
    LiquidLevelCardBasicConfigComponent,
    DoughnutBasicConfigComponent,
    RangeChartBasicConfigComponent,
    BarChartWithLabelsBasicConfigComponent,
    SingleSwitchBasicConfigComponent,
    ActionButtonBasicConfigComponent,
    CommandButtonBasicConfigComponent,
    PowerButtonBasicConfigComponent,
    SliderBasicConfigComponent,
    ToggleButtonBasicConfigComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    WidgetConfigComponentsModule
  ],
  exports: [
    WidgetActionsPanelComponent,
    SimpleCardBasicConfigComponent,
    EntitiesTableBasicConfigComponent,
    TimeseriesTableBasicConfigComponent,
    FlotBasicConfigComponent,
    AlarmsTableBasicConfigComponent,
    ValueCardBasicConfigComponent,
    AggregatedValueCardBasicConfigComponent,
    AggregatedDataKeyRowComponent,
    AggregatedDataKeysPanelComponent,
    DataKeyRowComponent,
    DataKeysPanelComponent,
    AlarmCountBasicConfigComponent,
    EntityCountBasicConfigComponent,
    BatteryLevelBasicConfigComponent,
    WindSpeedDirectionBasicConfigComponent,
    SignalStrengthBasicConfigComponent,
    ValueChartCardBasicConfigComponent,
    ProgressBarBasicConfigComponent,
    RadialGaugeBasicConfigComponent,
    ThermometerScaleGaugeBasicConfigComponent,
    CompassGaugeBasicConfigComponent,
    LiquidLevelCardBasicConfigComponent,
    DoughnutBasicConfigComponent,
    RangeChartBasicConfigComponent,
    BarChartWithLabelsBasicConfigComponent,
    SingleSwitchBasicConfigComponent,
    ActionButtonBasicConfigComponent,
    CommandButtonBasicConfigComponent,
    PowerButtonBasicConfigComponent,
    SliderBasicConfigComponent,
    ToggleButtonBasicConfigComponent
  ]
})
export class BasicWidgetConfigModule {
}

export const basicWidgetConfigComponentsMap: {[key: string]: Type<IBasicWidgetConfigComponent>} = {
  'tb-simple-card-basic-config': SimpleCardBasicConfigComponent,
  'tb-entities-table-basic-config': EntitiesTableBasicConfigComponent,
  'tb-timeseries-table-basic-config': TimeseriesTableBasicConfigComponent,
  'tb-flot-basic-config': FlotBasicConfigComponent,
  'tb-alarms-table-basic-config': AlarmsTableBasicConfigComponent,
  'tb-value-card-basic-config': ValueCardBasicConfigComponent,
  'tb-aggregated-value-card-basic-config': AggregatedValueCardBasicConfigComponent,
  'tb-alarm-count-basic-config': AlarmCountBasicConfigComponent,
  'tb-entity-count-basic-config': EntityCountBasicConfigComponent,
  'tb-battery-level-basic-config': BatteryLevelBasicConfigComponent,
  'tb-wind-speed-direction-basic-config': WindSpeedDirectionBasicConfigComponent,
  'tb-signal-strength-basic-config': SignalStrengthBasicConfigComponent,
  'tb-value-chart-card-basic-config': ValueChartCardBasicConfigComponent,
  'tb-progress-bar-basic-config': ProgressBarBasicConfigComponent,
  'tb-radial-gauge-basic-config': RadialGaugeBasicConfigComponent,
  'tb-thermometer-scale-gauge-basic-config': ThermometerScaleGaugeBasicConfigComponent,
  'tb-compass-gauge-basic-config': CompassGaugeBasicConfigComponent,
  'tb-liquid-level-card-basic-config': LiquidLevelCardBasicConfigComponent,
  'tb-doughnut-basic-config': DoughnutBasicConfigComponent,
  'tb-range-chart-basic-config': RangeChartBasicConfigComponent,
  'tb-bar-chart-with-labels-basic-config': BarChartWithLabelsBasicConfigComponent,
  'tb-single-switch-basic-config': SingleSwitchBasicConfigComponent,
  'tb-action-button-basic-config': ActionButtonBasicConfigComponent,
  'tb-command-button-basic-config': CommandButtonBasicConfigComponent,
  'tb-power-button-basic-config': PowerButtonBasicConfigComponent,
  'tb-slider-basic-config': SliderBasicConfigComponent,
  'tb-toggle-button-basic-config': ToggleButtonBasicConfigComponent
};
