

import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { ColorSettings, ColorType, colorTypeTranslations } from '@shared/models/widget-settings.models';
import { TbPopoverComponent } from '@shared/components/popover.component';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { deepClone } from '@core/utils';
import { WidgetService } from '@core/http/widget.service';
import { ColorSettingsComponent } from '@home/components/widget/lib/settings/common/color-settings.component';

@Component({
  selector: 'tb-color-settings-panel',
  templateUrl: './color-settings-panel.component.html',
  providers: [],
  styleUrls: ['./color-settings-panel.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ColorSettingsPanelComponent extends PageComponent implements OnInit {

  @Input()
  colorSettings: ColorSettings;

  @Input()
  popover: TbPopoverComponent<ColorSettingsPanelComponent>;

  @Input()
  settingsComponents: ColorSettingsComponent[];

  @Output()
  colorSettingsApplied = new EventEmitter<ColorSettings>();

  colorType = ColorType;

  colorTypes = Object.keys(ColorType) as ColorType[];

  colorTypeTranslationsMap = colorTypeTranslations;

  colorSettingsFormGroup: UntypedFormGroup;

  functionScopeVariables = this.widgetService.getWidgetScopeVariables();

  constructor(private fb: UntypedFormBuilder,
              private widgetService: WidgetService,
              protected store: Store<AppState>) {
    super(store);
  }

  ngOnInit(): void {
    this.colorSettingsFormGroup = this.fb.group(
      {
        type: [this.colorSettings?.type || ColorType.constant, []],
        color: [this.colorSettings?.color, []],
        rangeList: [this.colorSettings?.rangeList, []],
        colorFunction: [this.colorSettings?.colorFunction, []]
      }
    );
    this.colorSettingsFormGroup.get('type').valueChanges.subscribe(() => {
      setTimeout(() => {this.popover?.updatePosition();}, 0);
    });
  }

  copyColorSettings(comp: ColorSettingsComponent) {
    const sourceSettings = deepClone(comp.modelValue);
    this.colorSettings = sourceSettings;
    this.colorSettingsFormGroup.patchValue({
      type: this.colorSettings.type,
      color: this.colorSettings.color,
      colorFunction: this.colorSettings.colorFunction,
      rangeList: this.colorSettings.rangeList || []
    }, {emitEvent: false});
    this.colorSettingsFormGroup.markAsDirty();
  }

  cancel() {
    this.popover?.hide();
  }

  applyColorSettings() {
    const colorSettings = this.colorSettingsFormGroup.value;
    this.colorSettingsApplied.emit(colorSettings);
  }

}
