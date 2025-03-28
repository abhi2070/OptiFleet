

import { Component } from '@angular/core';
import { WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';

@Component({
  selector: 'tb-alarms-table-widget-settings',
  templateUrl: './alarms-table-widget-settings.component.html',
  styleUrls: ['./../widget-settings.scss']
})
export class AlarmsTableWidgetSettingsComponent extends WidgetSettingsComponent {

  alarmsTableWidgetSettingsForm: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  protected settingsForm(): UntypedFormGroup {
    return this.alarmsTableWidgetSettingsForm;
  }

  protected defaultSettings(): WidgetSettings {
    return {
      alarmsTitle: '',
      enableSelection: true,
      enableSearch: true,
      enableSelectColumnDisplay: true,
      enableFilter: true,
      enableStickyHeader: true,
      enableStickyAction: true,
      showCellActionsMenu: true,
      reserveSpaceForHiddenAction: 'true',
      displayDetails: true,
      allowAcknowledgment: true,
      allowClear: true,
      allowAssign: true,
      displayActivity: true,
      displayPagination: true,
      defaultPageSize: 10,
      defaultSortOrder: '-createdTime',
      useRowStyleFunction: false,
      rowStyleFunction: ''
    };
  }

  protected onSettingsSet(settings: WidgetSettings) {
    this.alarmsTableWidgetSettingsForm = this.fb.group({
      alarmsTitle: [settings.alarmsTitle, []],
      enableSelection: [settings.enableSelection, []],
      enableSearch: [settings.enableSearch, []],
      enableSelectColumnDisplay: [settings.enableSelectColumnDisplay, []],
      enableFilter: [settings.enableFilter, []],
      enableStickyHeader: [settings.enableStickyHeader, []],
      enableStickyAction: [settings.enableStickyAction, []],
      showCellActionsMenu: [settings.showCellActionsMenu, []],
      reserveSpaceForHiddenAction: [settings.reserveSpaceForHiddenAction, []],
      displayDetails: [settings.displayDetails, []],
      allowAcknowledgment: [settings.allowAcknowledgment, []],
      allowClear: [settings.allowClear, []],
      allowAssign: [settings.allowAssign, []],
      displayActivity: [settings.displayActivity, []],
      displayPagination: [settings.displayPagination, []],
      defaultPageSize: [settings.defaultPageSize, [Validators.min(1)]],
      defaultSortOrder: [settings.defaultSortOrder, []],
      useRowStyleFunction: [settings.useRowStyleFunction, []],
      rowStyleFunction: [settings.rowStyleFunction, [Validators.required]]
    });
  }

  protected validatorTriggers(): string[] {
    return ['useRowStyleFunction', 'displayPagination'];
  }

  protected updateValidators(emitEvent: boolean) {
    const useRowStyleFunction: boolean = this.alarmsTableWidgetSettingsForm.get('useRowStyleFunction').value;
    const displayPagination: boolean = this.alarmsTableWidgetSettingsForm.get('displayPagination').value;
    if (useRowStyleFunction) {
      this.alarmsTableWidgetSettingsForm.get('rowStyleFunction').enable();
    } else {
      this.alarmsTableWidgetSettingsForm.get('rowStyleFunction').disable();
    }
    if (displayPagination) {
      this.alarmsTableWidgetSettingsForm.get('defaultPageSize').enable();
    } else {
      this.alarmsTableWidgetSettingsForm.get('defaultPageSize').disable();
    }
    this.alarmsTableWidgetSettingsForm.get('rowStyleFunction').updateValueAndValidity({emitEvent});
    this.alarmsTableWidgetSettingsForm.get('defaultPageSize').updateValueAndValidity({emitEvent});
  }

}
