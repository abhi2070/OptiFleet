

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
  getValueActions,
  getValueActionTranslations,
  GetValueSettings
} from '@shared/models/action-widget-settings.models';
import { ValueType } from '@shared/models/constants';
import { TargetDevice, WidgetAction, widgetType } from '@shared/models/widget.models';
import { AttributeScope, DataKeyType, telemetryTypeTranslationsShort } from '@shared/models/telemetry/telemetry.models';
import { IAliasController } from '@core/api/widget-api.models';
import { WidgetService } from '@core/http/widget.service';
import { WidgetActionCallbacks } from '@home/components/widget/action/manage-widget-actions.component.models';

@Component({
  selector: 'tb-widget-action-settings-panel',
  templateUrl: './widget-action-settings-panel.component.html',
  providers: [],
  styleUrls: ['./action-settings-panel.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class WidgetActionSettingsPanelComponent extends PageComponent implements OnInit {

  @Input()
  widgetAction: WidgetAction;

  @Input()
  panelTitle: string;

  @Input()
  widgetType: widgetType;

  @Input()
  callbacks: WidgetActionCallbacks;

  @Input()
  popover: TbPopoverComponent<WidgetActionSettingsPanelComponent>;

  @Output()
  widgetActionApplied = new EventEmitter<WidgetAction>();

  widgetActionFormGroup: UntypedFormGroup;

  constructor(private fb: UntypedFormBuilder,
              protected store: Store<AppState>) {
    super(store);
  }

  ngOnInit(): void {
    this.widgetActionFormGroup = this.fb.group(
      {
        widgetAction: [this.widgetAction, []]
      }
    );
  }

  cancel() {
    this.popover?.hide();
  }

  applyWidgetAction() {
    const widgetAction: WidgetAction = this.widgetActionFormGroup.get('widgetAction').getRawValue();
    this.widgetActionApplied.emit(widgetAction);
  }
}
