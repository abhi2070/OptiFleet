

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { SharedHomeComponentsModule } from '@home/components/shared-home-components.module';
import { WidgetFontComponent } from '@home/components/widget/lib/settings/common/widget-font.component';
import { ValueSourceComponent } from '@home/components/widget/lib/settings/common/value-source.component';
import { LegendConfigComponent } from '@home/components/widget/lib/settings/common/legend-config.component';
import {
  ImageCardsSelectComponent,
  ImageCardsSelectOptionDirective
} from '@home/components/widget/lib/settings/common/image-cards-select.component';
import { FontSettingsComponent } from '@home/components/widget/lib/settings/common/font-settings.component';
import { FontSettingsPanelComponent } from '@home/components/widget/lib/settings/common/font-settings-panel.component';
import {
  ColorSettingsComponent,
  ColorSettingsComponentService
} from '@home/components/widget/lib/settings/common/color-settings.component';
import {
  ColorSettingsPanelComponent
} from '@home/components/widget/lib/settings/common/color-settings-panel.component';
import { CssUnitSelectComponent } from '@home/components/widget/lib/settings/common/css-unit-select.component';
import { DateFormatSelectComponent } from '@home/components/widget/lib/settings/common/date-format-select.component';
import {
  DateFormatSettingsPanelComponent
} from '@home/components/widget/lib/settings/common/date-format-settings-panel.component';
import { BackgroundSettingsComponent } from '@home/components/widget/lib/settings/common/background-settings.component';
import {
  BackgroundSettingsPanelComponent
} from '@home/components/widget/lib/settings/common/background-settings-panel.component';
import {
  CountWidgetSettingsComponent
} from '@home/components/widget/lib/settings/common/count-widget-settings.component';
import { ColorRangeListComponent } from '@home/components/widget/lib/settings/common/color-range-list.component';
import { ColorRangePanelComponent } from '@home/components/widget/lib/settings/common/color-range-panel.component';
import {
  ColorRangeSettingsComponent, ColorRangeSettingsComponentService
} from '@home/components/widget/lib/settings/common/color-range-settings.component';
import {
  GetValueActionSettingsComponent
} from '@home/components/widget/lib/settings/common/action/get-value-action-settings.component';
import {
  GetValueActionSettingsPanelComponent
} from '@home/components/widget/lib/settings/common/action/get-value-action-settings-panel.component';
import {
  DeviceKeyAutocompleteComponent
} from '@home/components/widget/lib/settings/control/device-key-autocomplete.component';
import {
  SetValueActionSettingsComponent
} from '@home/components/widget/lib/settings/common/action/set-value-action-settings.component';
import {
  SetValueActionSettingsPanelComponent
} from '@home/components/widget/lib/settings/common/action/set-value-action-settings-panel.component';
import { CssSizeInputComponent } from '@home/components/widget/lib/settings/common/css-size-input.component';
import { WidgetActionComponent } from '@home/components/widget/lib/settings/common/action/widget-action.component';
import {
  CustomActionPrettyResourcesTabsComponent
} from '@home/components/widget/lib/settings/common/action/custom-action-pretty-resources-tabs.component';
import {
  CustomActionPrettyEditorComponent
} from '@home/components/widget/lib/settings/common/action/custom-action-pretty-editor.component';
import {
  MobileActionEditorComponent
} from '@home/components/widget/lib/settings/common/action/mobile-action-editor.component';
import {
  WidgetActionSettingsComponent
} from '@home/components/widget/lib/settings/common/action/widget-action-settings.component';
import {
  WidgetActionSettingsPanelComponent
} from '@home/components/widget/lib/settings/common/action/widget-action-settings-panel.component';
import {
  WidgetButtonAppearanceComponent
} from '@home/components/widget/lib/settings/common/button/widget-button-appearance.component';
import {
  WidgetButtonCustomStyleComponent
} from '@home/components/widget/lib/settings/common/button/widget-button-custom-style.component';
import {
  WidgetButtonCustomStylePanelComponent
} from '@home/components/widget/lib/settings/common/button/widget-button-custom-style-panel.component';

@NgModule({
  declarations: [
    ImageCardsSelectOptionDirective,
    ImageCardsSelectComponent,
    FontSettingsComponent,
    FontSettingsPanelComponent,
    ColorSettingsComponent,
    ColorSettingsPanelComponent,
    CssUnitSelectComponent,
    CssSizeInputComponent,
    DateFormatSelectComponent,
    DateFormatSettingsPanelComponent,
    BackgroundSettingsComponent,
    BackgroundSettingsPanelComponent,
    ValueSourceComponent,
    LegendConfigComponent,
    WidgetFontComponent,
    CountWidgetSettingsComponent,
    ColorRangeListComponent,
    ColorRangePanelComponent,
    ColorRangeSettingsComponent,
    GetValueActionSettingsComponent,
    GetValueActionSettingsPanelComponent,
    DeviceKeyAutocompleteComponent,
    SetValueActionSettingsComponent,
    SetValueActionSettingsPanelComponent,
    WidgetActionComponent,
    CustomActionPrettyResourcesTabsComponent,
    CustomActionPrettyEditorComponent,
    MobileActionEditorComponent,
    WidgetActionSettingsComponent,
    WidgetActionSettingsPanelComponent,
    WidgetButtonAppearanceComponent,
    WidgetButtonCustomStyleComponent,
    WidgetButtonCustomStylePanelComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SharedHomeComponentsModule
  ],
  exports: [
    ImageCardsSelectOptionDirective,
    ImageCardsSelectComponent,
    FontSettingsComponent,
    FontSettingsPanelComponent,
    ColorSettingsComponent,
    ColorSettingsPanelComponent,
    CssUnitSelectComponent,
    CssSizeInputComponent,
    DateFormatSelectComponent,
    DateFormatSettingsPanelComponent,
    BackgroundSettingsComponent,
    BackgroundSettingsPanelComponent,
    ValueSourceComponent,
    LegendConfigComponent,
    WidgetFontComponent,
    CountWidgetSettingsComponent,
    ColorRangeListComponent,
    ColorRangePanelComponent,
    ColorRangeSettingsComponent,
    GetValueActionSettingsComponent,
    GetValueActionSettingsPanelComponent,
    DeviceKeyAutocompleteComponent,
    SetValueActionSettingsComponent,
    SetValueActionSettingsPanelComponent,
    WidgetActionComponent,
    CustomActionPrettyResourcesTabsComponent,
    CustomActionPrettyEditorComponent,
    MobileActionEditorComponent,
    WidgetActionSettingsComponent,
    WidgetActionSettingsPanelComponent,
    WidgetButtonAppearanceComponent,
    WidgetButtonCustomStyleComponent,
    WidgetButtonCustomStylePanelComponent
  ],
  providers: [
    ColorSettingsComponentService,
    ColorRangeSettingsComponentService
  ]
})
export class WidgetSettingsCommonModule {
}
