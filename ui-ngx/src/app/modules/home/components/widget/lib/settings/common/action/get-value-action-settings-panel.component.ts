

import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { TbPopoverComponent } from '@shared/components/popover.component';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { merge } from 'rxjs';
import {
  DataToValueType,
  GetValueAction,
  getValueActionsByWidgetType,
  getValueActionTranslations,
  GetValueSettings
} from '@shared/models/action-widget-settings.models';
import { ValueType } from '@shared/models/constants';
import { TargetDevice, widgetType } from '@shared/models/widget.models';
import { AttributeScope, DataKeyType, telemetryTypeTranslationsShort } from '@shared/models/telemetry/telemetry.models';
import { IAliasController } from '@core/api/widget-api.models';
import { WidgetService } from '@core/http/widget.service';

@Component({
  selector: 'tb-get-value-action-settings-panel',
  templateUrl: './get-value-action-settings-panel.component.html',
  providers: [],
  styleUrls: ['./action-settings-panel.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class GetValueActionSettingsPanelComponent extends PageComponent implements OnInit {

  @Input()
  getValueSettings: GetValueSettings<any>;

  @Input()
  panelTitle: string;

  @Input()
  valueType: ValueType;

  @Input()
  trueLabel = 'value.true';

  @Input()
  falseLabel = 'value.false';

  @Input()
  stateLabel: string;

  @Input()
  aliasController: IAliasController;

  @Input()
  targetDevice: TargetDevice;

  @Input()
  widgetType: widgetType;

  @Input()
  popover: TbPopoverComponent<GetValueActionSettingsPanelComponent>;

  @Output()
  getValueSettingsApplied = new EventEmitter<GetValueSettings<any>>();

  getValueAction = GetValueAction;

  getValueActions: GetValueAction[];

  getValueActionTranslationsMap = getValueActionTranslations;

  telemetryTypeTranslationsMap = telemetryTypeTranslationsShort;

  attributeScopes = Object.keys(AttributeScope) as AttributeScope[];

  dataKeyType = DataKeyType;

  dataToValueType = DataToValueType;

  functionScopeVariables = this.widgetService.getWidgetScopeVariables();

  ValueType = ValueType;

  getValueSettingsFormGroup: UntypedFormGroup;

  constructor(private fb: UntypedFormBuilder,
              private widgetService: WidgetService,
              protected store: Store<AppState>) {
    super(store);
  }

  ngOnInit(): void {
    this.getValueActions = getValueActionsByWidgetType(this.widgetType);
    this.getValueSettingsFormGroup = this.fb.group(
      {
        action: [this.getValueSettings?.action, []],
        defaultValue: [this.getValueSettings?.defaultValue, [Validators.required]],
        executeRpc: this.fb.group({
          method: [this.getValueSettings?.executeRpc?.method, [Validators.required]],
          requestTimeout: [this.getValueSettings?.executeRpc?.requestTimeout, [Validators.required, Validators.min(5000)]],
          requestPersistent: [this.getValueSettings?.executeRpc?.requestPersistent, []],
          persistentPollingInterval:
            [this.getValueSettings?.executeRpc?.persistentPollingInterval, [Validators.required, Validators.min(1000)]]
        }),
        getAttribute: this.fb.group({
          scope: [this.getValueSettings?.getAttribute?.scope, []],
          key: [this.getValueSettings?.getAttribute?.key, [Validators.required]]
        }),
        getTimeSeries: this.fb.group({
          key: [this.getValueSettings?.getTimeSeries?.key, [Validators.required]]
        }),
        dataToValue: this.fb.group({
          type: [this.getValueSettings?.dataToValue?.type, [Validators.required]],
          dataToValueFunction: [this.getValueSettings?.dataToValue?.dataToValueFunction, [Validators.required]],
        }),
      }
    );
    if (this.valueType === ValueType.BOOLEAN) {
      (this.getValueSettingsFormGroup.get('dataToValue') as UntypedFormGroup).addControl(
        'compareToValue', this.fb.control(this.getValueSettings?.dataToValue?.compareToValue, [Validators.required])
      );
    }

    merge(this.getValueSettingsFormGroup.get('action').valueChanges,
          this.getValueSettingsFormGroup.get('dataToValue').get('type').valueChanges,
          this.getValueSettingsFormGroup.get('executeRpc').get('requestPersistent').valueChanges).subscribe(() => {
      this.updateValidators();
    });
    this.updateValidators();
  }

  cancel() {
    this.popover?.hide();
  }

  applyGetValueSettings() {
    const getValueSettings: GetValueSettings<any> = this.getValueSettingsFormGroup.getRawValue();
    this.getValueSettingsApplied.emit(getValueSettings);
  }

  private updateValidators() {
    const action: GetValueAction = this.getValueSettingsFormGroup.get('action').value;
    const dataToValueType: DataToValueType = this.getValueSettingsFormGroup.get('dataToValue').get('type').value;

    this.getValueSettingsFormGroup.get('defaultValue').disable({emitEvent: false});
    this.getValueSettingsFormGroup.get('executeRpc').disable({emitEvent: false});
    this.getValueSettingsFormGroup.get('getAttribute').disable({emitEvent: false});
    this.getValueSettingsFormGroup.get('getTimeSeries').disable({emitEvent: false});
    switch (action) {
      case GetValueAction.DO_NOTHING:
        this.getValueSettingsFormGroup.get('defaultValue').enable({emitEvent: false});
        break;
      case GetValueAction.EXECUTE_RPC:
        this.getValueSettingsFormGroup.get('executeRpc').enable({emitEvent: false});
        const requestPersistent: boolean = this.getValueSettingsFormGroup.get('executeRpc').get('requestPersistent').value;
        if (requestPersistent) {
          this.getValueSettingsFormGroup.get('executeRpc').get('persistentPollingInterval').enable({emitEvent: false});
        } else {
          this.getValueSettingsFormGroup.get('executeRpc').get('persistentPollingInterval').disable({emitEvent: false});
        }
        break;
      case GetValueAction.GET_ATTRIBUTE:
        this.getValueSettingsFormGroup.get('getAttribute').enable({emitEvent: false});
        break;
      case GetValueAction.GET_TIME_SERIES:
        this.getValueSettingsFormGroup.get('getTimeSeries').enable({emitEvent: false});
        break;
    }
    if (action === GetValueAction.DO_NOTHING) {
      this.getValueSettingsFormGroup.get('dataToValue').disable({emitEvent: false});
    } else {
      this.getValueSettingsFormGroup.get('dataToValue').enable({emitEvent: false});
      if (dataToValueType === DataToValueType.FUNCTION) {
        this.getValueSettingsFormGroup.get('dataToValue').get('dataToValueFunction').enable({emitEvent: false});
      } else {
        this.getValueSettingsFormGroup.get('dataToValue').get('dataToValueFunction').disable({emitEvent: false});
      }
    }
  }
}
