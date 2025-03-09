

import { Component } from '@angular/core';
import { WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';

@Component({
  selector: 'tb-doc-links-widget-settings',
  templateUrl: './doc-links-widget-settings.component.html',
  styleUrls: ['./../widget-settings.scss']
})
export class DocLinksWidgetSettingsComponent extends WidgetSettingsComponent {

  docLinksWidgetSettingsForm: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  protected settingsForm(): UntypedFormGroup {
    return this.docLinksWidgetSettingsForm;
  }

  protected defaultSettings(): WidgetSettings {
    return {
      columns: 3
    };
  }

  protected onSettingsSet(settings: WidgetSettings) {
    this.docLinksWidgetSettingsForm = this.fb.group({
      columns: [settings.columns, [Validators.required, Validators.min(1), Validators.max(20)]]
    });
  }
}
