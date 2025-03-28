

import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { MatDialog } from '@angular/material/dialog';
import { Dashboard, WidgetLayout } from '@shared/models/dashboard.models';
import { IAliasController, IStateController } from '@core/api/widget-api.models';
import { Widget, WidgetConfigMode } from '@shared/models/widget.models';
import { WidgetComponentService } from '@home/components/widget/widget-component.service';
import { WidgetConfigComponentData } from '../../models/widget-component.models';
import { isDefined, isDefinedAndNotNull, isString } from '@core/utils';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { WidgetConfigComponent } from '@home/components/widget/widget-config.component';

@Component({
  selector: 'tb-edit-widget',
  templateUrl: './edit-widget.component.html',
  styleUrls: ['./edit-widget.component.scss']
})
export class EditWidgetComponent extends PageComponent implements OnInit, OnChanges {

  @ViewChild('widgetConfigComponent')
  widgetConfigComponent: WidgetConfigComponent;

  @Input()
  dashboard: Dashboard;

  @Input()
  aliasController: IAliasController;

  @Input()
  stateController: IStateController;

  @Input()
  widgetEditMode: boolean;

  @Input()
  widget: Widget;

  @Input()
  widgetLayout: WidgetLayout;

  @Output()
  applyWidgetConfig = new EventEmitter<void>();

  @Output()
  revertWidgetConfig = new EventEmitter<void>();

  widgetFormGroup: UntypedFormGroup;

  widgetConfig: WidgetConfigComponentData;

  previewMode = false;

  get widgetConfigMode(): WidgetConfigMode {
    return this.widgetConfigComponent?.widgetConfigMode;
  }

  set widgetConfigMode(widgetConfigMode: WidgetConfigMode) {
    this.widgetConfigComponent.setWidgetConfigMode(widgetConfigMode);
  }

  private currentWidgetConfigChanged = false;

  constructor(protected store: Store<AppState>,
              private dialog: MatDialog,
              private fb: UntypedFormBuilder,
              private widgetComponentService: WidgetComponentService) {
    super(store);
    this.widgetFormGroup = this.fb.group({
      widgetConfig: [null]
    });
  }

  ngOnInit(): void {
    this.loadWidgetConfig();
  }

  ngOnChanges(changes: SimpleChanges): void {
    let reloadConfig = false;
    for (const propName of Object.keys(changes)) {
      const change = changes[propName];
      if (!change.firstChange && change.currentValue !== change.previousValue) {
        if (['widget', 'widgetLayout'].includes(propName)) {
          reloadConfig = true;
        }
      }
    }
    if (reloadConfig) {
      if (this.currentWidgetConfigChanged) {
        this.currentWidgetConfigChanged = false;
      } else {
        this.previewMode = false;
      }
      this.loadWidgetConfig();
    }
  }

  onApplyWidgetConfig() {
    if (this.widgetFormGroup.valid) {
      this.currentWidgetConfigChanged = true;
      this.applyWidgetConfig.emit();
    }
  }

  onRevertWidgetConfig() {
    this.currentWidgetConfigChanged = true;
    this.revertWidgetConfig.emit();
  }

  private loadWidgetConfig() {
    if (!this.widget) {
      return;
    }
    const widgetInfo = this.widgetComponentService.getInstantWidgetInfo(this.widget);
    const rawSettingsSchema = widgetInfo.typeSettingsSchema || widgetInfo.settingsSchema;
    const rawDataKeySettingsSchema = widgetInfo.typeDataKeySettingsSchema || widgetInfo.dataKeySettingsSchema;
    const rawLatestDataKeySettingsSchema = widgetInfo.typeLatestDataKeySettingsSchema || widgetInfo.latestDataKeySettingsSchema;
    const typeParameters = widgetInfo.typeParameters;
    const actionSources = widgetInfo.actionSources;
    const isDataEnabled = isDefined(widgetInfo.typeParameters) ? !widgetInfo.typeParameters.useCustomDatasources : true;
    let settingsSchema;
    if (!rawSettingsSchema || rawSettingsSchema === '') {
      settingsSchema = {};
    } else {
      settingsSchema = isString(rawSettingsSchema) ? JSON.parse(rawSettingsSchema) : rawSettingsSchema;
    }
    let dataKeySettingsSchema;
    if (!rawDataKeySettingsSchema || rawDataKeySettingsSchema === '') {
      dataKeySettingsSchema = {};
    } else {
      dataKeySettingsSchema = isString(rawDataKeySettingsSchema) ? JSON.parse(rawDataKeySettingsSchema) : rawDataKeySettingsSchema;
    }
    let latestDataKeySettingsSchema;
    if (!rawLatestDataKeySettingsSchema || rawLatestDataKeySettingsSchema === '') {
      latestDataKeySettingsSchema = {};
    } else {
      latestDataKeySettingsSchema = isString(rawLatestDataKeySettingsSchema) ?
        JSON.parse(rawLatestDataKeySettingsSchema) : rawLatestDataKeySettingsSchema;
    }
    this.widgetConfig = {
      widgetName: widgetInfo.widgetName,
      config: this.widget.config,
      layout: this.widgetLayout,
      widgetType: this.widget.type,
      typeParameters,
      actionSources,
      isDataEnabled,
      settingsSchema,
      dataKeySettingsSchema,
      latestDataKeySettingsSchema,
      settingsDirective: widgetInfo.settingsDirective,
      dataKeySettingsDirective: widgetInfo.dataKeySettingsDirective,
      latestDataKeySettingsDirective: widgetInfo.latestDataKeySettingsDirective,
      hasBasicMode: isDefinedAndNotNull(widgetInfo.hasBasicMode) ? widgetInfo.hasBasicMode : false,
      basicModeDirective: widgetInfo.basicModeDirective
    };
    this.widgetFormGroup.reset({widgetConfig: this.widgetConfig});
  }
}
