/* eslint-disable max-len */
import { AfterViewInit, Component, Input, OnInit, ViewChild } from '@angular/core';
import { SimulationHelperComponent } from '@app/shared/components/simulation-helper.component';
import { AppState } from '@core/core.state';
import {
  formattedDataFormDatasourceData,
  mergeFormattedData
} from '@core/utils';
import { WidgetContext } from '@home/models/widget-component.models';
import { Store } from '@ngrx/store';
import { PageComponent } from '@shared/components/page.component';
import { ThreedSimulationWidgetSettings } from './threed/threed-models';

@Component({
  selector: 'tb-threed-simulation-widget',
  templateUrl: './threed-simulation-widget.component.html',
  styleUrls: ['./threed-simulation-widget.component.scss']
})
export class ThreedSimulationWidgetComponent extends PageComponent implements OnInit, AfterViewInit {

  @ViewChild('simulationHelper') simulationHelper?: SimulationHelperComponent;

  @Input()
  ctx: WidgetContext;

  settings: ThreedSimulationWidgetSettings;

  constructor(
    protected store: Store<AppState>
  ) {
    super(store);
  }

  ngOnInit() {
    this.ctx.$scope.threedSimulationWidget = this;
    this.settings = this.ctx.settings;
  }

  ngAfterViewInit() {
    this.simulationHelper?.updateSettings(this.settings);
  }

  public onDataUpdate() {
    const data = this.ctx.data;
    let formattedData = formattedDataFormDatasourceData(data);
    if (this.ctx.latestData && this.ctx.latestData.length) {
      const formattedLatestData = formattedDataFormDatasourceData(this.ctx.latestData);
      formattedData = mergeFormattedData(formattedData, formattedLatestData);
    }

    this.simulationHelper?.onDataUpdate(formattedData);
  }

  public onEditModeChanged() {
    this.simulationHelper?.onEditModeChanged(!this.ctx.isEdit);
  }

  public onResize(width: number, height: number): void {
    this.simulationHelper?.onResize(width, height);
  }

}
