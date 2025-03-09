

import { Component } from '@angular/core';
import { WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { defaultThreedSceneSettings, ThreedViewWidgetSettings } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { ThreedSceneControllerType } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-constants';

@Component({
  selector: 'tb-threed-navigation-widget-settings',
  templateUrl: './threed-navigation-widget-settings.component.html',
  styleUrls: ['./../widget-settings.scss']
})
export class ThreedNavigationWidgetSettingsComponent extends WidgetSettingsComponent {

  threedViewWidgetSettingsForm: FormGroup;
  ThreedSceneControllerType = ThreedSceneControllerType;

  constructor(protected store: Store<AppState>,
              private fb: FormBuilder) {
    super(store);
  }

  protected settingsForm(): FormGroup {
    return this.threedViewWidgetSettingsForm;
  }

  protected override defaultSettings(): WidgetSettings {    
    return {
      hoverColor: "rgba(255,0,0,0.5)",
      threedSceneSettings:  defaultThreedSceneSettings
    };
  }

  protected onSettingsSet(settings: WidgetSettings) {
    const t_settings = settings as ThreedViewWidgetSettings;

    this.threedViewWidgetSettingsForm = this.fb.group({
      hoverColor: [t_settings.hoverColor, [Validators.required]],
      threedSceneSettings: [t_settings.threedSceneSettings, []],
    });
  }
}


