

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';
import { EntitiesTableWidgetComponent } from '@home/components/widget/lib/entity/entities-table-widget.component';
import { DisplayColumnsPanelComponent } from '@home/components/widget/lib/display-columns-panel.component';
import { AlarmsTableWidgetComponent } from '@home/components/widget/lib/alarm/alarms-table-widget.component';
import { SharedHomeComponentsModule } from '@home/components/shared-home-components.module';
import { TimeseriesTableWidgetComponent } from '@home/components/widget/lib/timeseries-table-widget.component';
import { EntitiesHierarchyWidgetComponent } from '@home/components/widget/lib/entity/entities-hierarchy-widget.component';
import { RpcWidgetsModule } from '@home/components/widget/lib/rpc/rpc-widgets.module';
import {
  DateRangeNavigatorPanelComponent,
  DateRangeNavigatorWidgetComponent
} from '@home/components/widget/lib/date-range-navigator/date-range-navigator.component';
import { MultipleInputWidgetComponent } from '@home/components/widget/lib/multiple-input-widget.component';
import { TripAnimationComponent } from '@home/components/widget/lib/trip-animation/trip-animation.component';
import { PhotoCameraInputWidgetComponent } from '@home/components/widget/lib/photo-camera-input.component';
import { GatewayFormComponent } from '@home/components/widget/lib/gateway/gateway-form.component';
import { NavigationCardsWidgetComponent } from '@home/components/widget/lib/navigation-cards-widget.component';
import { NavigationCardWidgetComponent } from '@home/components/widget/lib/navigation-card-widget.component';
import { EdgesOverviewWidgetComponent } from '@home/components/widget/lib/edges-overview-widget.component';
import { JsonInputWidgetComponent } from '@home/components/widget/lib/json-input-widget.component';
import { QrCodeWidgetComponent } from '@home/components/widget/lib/qrcode-widget.component';
import { MarkdownWidgetComponent } from '@home/components/widget/lib/markdown-widget.component';
import { SelectEntityDialogComponent } from '@home/components/widget/lib/maps/dialogs/select-entity-dialog.component';
import { HomePageWidgetsModule } from '@home/components/widget/lib/home-page/home-page-widgets.module';
import { WIDGET_COMPONENTS_MODULE_TOKEN } from '@home/components/tokens';
import { FlotWidgetComponent } from '@home/components/widget/lib/flot-widget.component';
import { LegendComponent } from '@home/components/widget/lib/legend.component';
import { GatewayConnectorComponent } from '@home/components/widget/lib/gateway/gateway-connectors.component';
import { GatewayLogsComponent } from '@home/components/widget/lib/gateway/gateway-logs.component';
import { GatewayStatisticsComponent } from '@home/components/widget/lib/gateway/gateway-statistics.component';
import { GatewayServiceRPCComponent } from '@home/components/widget/lib/gateway/gateway-service-rpc.component';
import { DeviceGatewayCommandComponent } from '@home/components/widget/lib/gateway/device-gateway-command.component';
import { GatewayConfigurationComponent } from '@home/components/widget/lib/gateway/gateway-configuration.component';
import {
  GatewayRemoteConfigurationDialogComponent
} from '@home/components/widget/lib/gateway/gateway-remote-configuration-dialog';
import { ValueCardWidgetComponent } from '@home/components/widget/lib/cards/value-card-widget.component';
import {
  AggregatedValueCardWidgetComponent
} from '@home/components/widget/lib/cards/aggregated-value-card-widget.component';
import { CountWidgetComponent } from '@home/components/widget/lib/count/count-widget.component';
import { BatteryLevelWidgetComponent } from '@home/components/widget/lib/indicator/battery-level-widget.component';
import {
  WindSpeedDirectionWidgetComponent
} from '@home/components/widget/lib/weather/wind-speed-direction-widget.component';
import { SignalStrengthWidgetComponent } from '@home/components/widget/lib/indicator/signal-strength-widget.component';
import { ValueChartCardWidgetComponent } from '@home/components/widget/lib/cards/value-chart-card-widget.component';
import { ProgressBarWidgetComponent } from '@home/components/widget/lib/cards/progress-bar-widget.component';
import { LiquidLevelWidgetComponent } from '@home/components/widget/lib/indicator/liquid-level-widget.component';
import { DoughnutWidgetComponent } from '@home/components/widget/lib/chart/doughnut-widget.component';
import { RangeChartWidgetComponent } from '@home/components/widget/lib/chart/range-chart-widget.component';
import {
  BarChartWithLabelsWidgetComponent
} from '@home/components/widget/lib/chart/bar-chart-with-labels-widget.component';
import { SingleSwitchWidgetComponent } from '@home/components/widget/lib/rpc/single-switch-widget.component';
import { ActionButtonWidgetComponent } from '@home/components/widget/lib/button/action-button-widget.component';
import { CommandButtonWidgetComponent } from '@home/components/widget/lib/button/command-button-widget.component';
import { PowerButtonWidgetComponent } from '@home/components/widget/lib/rpc/power-button-widget.component';
import { SliderWidgetComponent } from '@home/components/widget/lib/rpc/slider-widget.component';
import { ToggleButtonWidgetComponent } from '@home/components/widget/lib/button/toggle-button-widget.component';

import { ThreedNavigationWidgetComponent } from '@home/components/widget/threed-view-widget/threed-navigation-widget.component';
import { ThreedOrbitWidgetComponent } from '@home/components/widget/threed-view-widget/threed-orbit-widget.component';
import { ThreedSimulationWidgetComponent } from '@home/components/widget/threed-view-widget/threed-simulation-widget.component';


@NgModule({
  declarations:
    [
      DisplayColumnsPanelComponent,
      EntitiesTableWidgetComponent,
      AlarmsTableWidgetComponent,
      TimeseriesTableWidgetComponent,
      EntitiesHierarchyWidgetComponent,
      EdgesOverviewWidgetComponent,
      DateRangeNavigatorWidgetComponent,
      DateRangeNavigatorPanelComponent,
      JsonInputWidgetComponent,
      MultipleInputWidgetComponent,
      TripAnimationComponent,
      PhotoCameraInputWidgetComponent,
      GatewayFormComponent,
      NavigationCardsWidgetComponent,
      NavigationCardWidgetComponent,
      QrCodeWidgetComponent,
      MarkdownWidgetComponent,
      SelectEntityDialogComponent,
      LegendComponent,
      FlotWidgetComponent,
      GatewayConnectorComponent,
      GatewayLogsComponent,
      GatewayStatisticsComponent,
      GatewayServiceRPCComponent,
      DeviceGatewayCommandComponent,
      GatewayConfigurationComponent,
      GatewayRemoteConfigurationDialogComponent,
      ValueCardWidgetComponent,
      AggregatedValueCardWidgetComponent,
      CountWidgetComponent,
      BatteryLevelWidgetComponent,
      WindSpeedDirectionWidgetComponent,
      SignalStrengthWidgetComponent,
      ValueChartCardWidgetComponent,
      ProgressBarWidgetComponent,
      LiquidLevelWidgetComponent,
      DoughnutWidgetComponent,
      RangeChartWidgetComponent,
      BarChartWithLabelsWidgetComponent,
      SingleSwitchWidgetComponent,
      ActionButtonWidgetComponent,
      CommandButtonWidgetComponent,
      PowerButtonWidgetComponent,
      SliderWidgetComponent,
      ToggleButtonWidgetComponent,
      ThreedNavigationWidgetComponent,
      ThreedOrbitWidgetComponent,
      ThreedSimulationWidgetComponent
    ],
  imports: [
    CommonModule,
    SharedModule,
    RpcWidgetsModule,
    HomePageWidgetsModule,
    SharedHomeComponentsModule
  ],
    exports: [
        EntitiesTableWidgetComponent,
        AlarmsTableWidgetComponent,
        TimeseriesTableWidgetComponent,
        EntitiesHierarchyWidgetComponent,
        EdgesOverviewWidgetComponent,
        RpcWidgetsModule,
        HomePageWidgetsModule,
        DateRangeNavigatorWidgetComponent,
        JsonInputWidgetComponent,
        MultipleInputWidgetComponent,
        TripAnimationComponent,
        PhotoCameraInputWidgetComponent,
        GatewayFormComponent,
        NavigationCardsWidgetComponent,
        NavigationCardWidgetComponent,
        QrCodeWidgetComponent,
        MarkdownWidgetComponent,
        LegendComponent,
        FlotWidgetComponent,
        GatewayConnectorComponent,
        GatewayLogsComponent,
        GatewayStatisticsComponent,
        GatewayServiceRPCComponent,
        DeviceGatewayCommandComponent,
        GatewayConfigurationComponent,
        GatewayRemoteConfigurationDialogComponent,
        ValueCardWidgetComponent,
        AggregatedValueCardWidgetComponent,
        CountWidgetComponent,
        BatteryLevelWidgetComponent,
        WindSpeedDirectionWidgetComponent,
        SignalStrengthWidgetComponent,
        ValueChartCardWidgetComponent,
        ProgressBarWidgetComponent,
        LiquidLevelWidgetComponent,
        DoughnutWidgetComponent,
        RangeChartWidgetComponent,
        BarChartWithLabelsWidgetComponent,
        SingleSwitchWidgetComponent,
        ActionButtonWidgetComponent,
        CommandButtonWidgetComponent,
        PowerButtonWidgetComponent,
        SliderWidgetComponent,
        ToggleButtonWidgetComponent,
        ThreedNavigationWidgetComponent,
        ThreedOrbitWidgetComponent,
        ThreedSimulationWidgetComponent
    ],
  providers: [
    {provide: WIDGET_COMPONENTS_MODULE_TOKEN, useValue: WidgetComponentsModule }
  ]
})
export class WidgetComponentsModule {
}
