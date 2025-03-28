

import { Component } from '@angular/core';
import { WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';

@Component({
  selector: 'tb-timeseries-table-widget-settings',
  templateUrl: './timeseries-table-widget-settings.component.html',
  styleUrls: ['./../widget-settings.scss']
})
export class TimeseriesTableWidgetSettingsComponent extends WidgetSettingsComponent {

  timeseriesTableWidgetSettingsForm: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  protected settingsForm(): UntypedFormGroup {
    return this.timeseriesTableWidgetSettingsForm;
  }

  protected defaultSettings(): WidgetSettings {
    return {
      enableSearch: true,
      enableSelectColumnDisplay: true,
      enableStickyHeader: true,
      enableStickyAction: true,
      showCellActionsMenu: true,
      reserveSpaceForHiddenAction: 'true',
      showTimestamp: true,
      dateFormat: {format: 'yyyy-MM-dd HH:mm:ss'},
      displayPagination: true,
      useEntityLabel: false,
      defaultPageSize: 10,
      hideEmptyLines: false,
      disableStickyHeader: false,
      useRowStyleFunction: false,
      rowStyleFunction: ''
    };
  }

  protected onSettingsSet(settings: WidgetSettings) {
    // For backward compatibility
    const dateFormat = settings.dateFormat;
    if (settings?.showMilliseconds) {
      dateFormat.format = 'yyyy-MM-dd HH:mm:ss.SSS';
    }

    this.timeseriesTableWidgetSettingsForm = this.fb.group({
      enableSearch: [settings.enableSearch, []],
      enableSelectColumnDisplay: [settings.enableSelectColumnDisplay, []],
      enableStickyHeader: [settings.enableStickyHeader, []],
      enableStickyAction: [settings.enableStickyAction, []],
      showCellActionsMenu: [settings.showCellActionsMenu, []],
      reserveSpaceForHiddenAction: [settings.reserveSpaceForHiddenAction, []],
      showTimestamp: [settings.showTimestamp, []],
      dateFormat: [dateFormat, []],
      displayPagination: [settings.displayPagination, []],
      useEntityLabel: [settings.useEntityLabel, []],
      defaultPageSize: [settings.defaultPageSize, [Validators.min(1)]],
      hideEmptyLines: [settings.hideEmptyLines, []],
      disableStickyHeader: [settings.disableStickyHeader, []],
      useRowStyleFunction: [settings.useRowStyleFunction, []],
      rowStyleFunction: [settings.rowStyleFunction, [Validators.required]]
    });
  }

  protected validatorTriggers(): string[] {
    return ['useRowStyleFunction', 'displayPagination'];
  }

  protected updateValidators(emitEvent: boolean) {
    const useRowStyleFunction: boolean = this.timeseriesTableWidgetSettingsForm.get('useRowStyleFunction').value;
    const displayPagination: boolean = this.timeseriesTableWidgetSettingsForm.get('displayPagination').value;
    if (useRowStyleFunction) {
      this.timeseriesTableWidgetSettingsForm.get('rowStyleFunction').enable();
    } else {
      this.timeseriesTableWidgetSettingsForm.get('rowStyleFunction').disable();
    }
    if (displayPagination) {
      this.timeseriesTableWidgetSettingsForm.get('defaultPageSize').enable();
    } else {
      this.timeseriesTableWidgetSettingsForm.get('defaultPageSize').disable();
    }
    this.timeseriesTableWidgetSettingsForm.get('rowStyleFunction').updateValueAndValidity({emitEvent});
    this.timeseriesTableWidgetSettingsForm.get('defaultPageSize').updateValueAndValidity({emitEvent});
  }

}
