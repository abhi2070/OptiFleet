

import { Component } from '@angular/core';
import { WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { MatChipInputEvent } from '@angular/material/chips';
import { COMMA, ENTER, SEMICOLON } from '@angular/cdk/keycodes';

@Component({
  selector: 'tb-gateway-events-widget-settings',
  templateUrl: './gateway-events-widget-settings.component.html',
  styleUrls: ['./../widget-settings.scss']
})
export class GatewayEventsWidgetSettingsComponent extends WidgetSettingsComponent {

  separatorKeysCodes = [ENTER, COMMA, SEMICOLON];

  gatewayEventsWidgetSettingsForm: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  protected settingsForm(): UntypedFormGroup {
    return this.gatewayEventsWidgetSettingsForm;
  }

  protected defaultSettings(): WidgetSettings {
    return {
      eventsTitle: 'Gateway events form title',
      eventsReg: []
    };
  }

  protected onSettingsSet(settings: WidgetSettings) {
    this.gatewayEventsWidgetSettingsForm = this.fb.group({
      eventsTitle: [settings.eventsTitle, []],
      eventsReg: [settings.eventsReg, []]
    });
  }

  removeEventFilter(eventFilter: string) {
    const eventsFilter: string[] = this.gatewayEventsWidgetSettingsForm.get('eventsReg').value;
    const index = eventsFilter.indexOf(eventFilter);
    if (index > -1) {
      eventsFilter.splice(index, 1);
      this.gatewayEventsWidgetSettingsForm.get('eventsReg').setValue(eventsFilter);
      this.gatewayEventsWidgetSettingsForm.get('eventsReg').markAsDirty();
    }
  }

  addEventFilterFromInput(event: MatChipInputEvent) {
    const value = event.value;
    if ((value || '').trim()) {
      const eventsFilter: string[] = this.gatewayEventsWidgetSettingsForm.get('eventsReg').value;
      const index = eventsFilter.indexOf(value);
      if (index === -1) {
        eventsFilter.push(value);
        this.gatewayEventsWidgetSettingsForm.get('eventsReg').setValue(eventsFilter);
        this.gatewayEventsWidgetSettingsForm.get('eventsReg').markAsDirty();
      }
      event.chipInput.clear();
    }
  }
}
