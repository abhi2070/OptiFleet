

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';
import { AlarmFilterConfigComponent } from '@home/components/alarm/alarm-filter-config.component';
import { AlarmAssigneeSelectComponent } from '@home/components/alarm/alarm-assignee-select.component';
import { DataKeysComponent } from '@home/components/widget/config/data-keys.component';
import { DataKeyConfigDialogComponent } from '@home/components/widget/config/data-key-config-dialog.component';
import { DataKeyConfigComponent } from '@home/components/widget/config/data-key-config.component';
import { DatasourceComponent } from '@home/components/widget/config/datasource.component';
import { DatasourcesComponent } from '@home/components/widget/config/datasources.component';
import { EntityAliasSelectComponent } from '@home/components/alias/entity-alias-select.component';
import { FilterSelectComponent } from '@home/components/filter/filter-select.component';
import { WidgetSettingsModule } from '@home/components/widget/lib/settings/widget-settings.module';
import { WidgetSettingsComponent } from '@home/components/widget/config/widget-settings.component';
import { TimewindowConfigPanelComponent } from '@home/components/widget/config/timewindow-config-panel.component';
import { WidgetSettingsCommonModule } from '@home/components/widget/lib/settings/common/widget-settings-common.module';
import { TimewindowStyleComponent } from '@home/components/widget/config/timewindow-style.component';
import { TimewindowStylePanelComponent } from '@home/components/widget/config/timewindow-style-panel.component';
import { TargetDeviceComponent } from '@home/components/widget/config/target-device.component';

@NgModule({
  declarations:
    [
      AlarmAssigneeSelectComponent,
      AlarmFilterConfigComponent,
      DataKeysComponent,
      DataKeyConfigDialogComponent,
      DataKeyConfigComponent,
      DatasourceComponent,
      DatasourcesComponent,
      TargetDeviceComponent,
      EntityAliasSelectComponent,
      FilterSelectComponent,
      TimewindowStyleComponent,
      TimewindowStylePanelComponent,
      TimewindowConfigPanelComponent,
      WidgetSettingsComponent
    ],
  imports: [
    CommonModule,
    SharedModule,
    WidgetSettingsModule,
    WidgetSettingsCommonModule
  ],
  exports: [
    AlarmAssigneeSelectComponent,
    AlarmFilterConfigComponent,
    DataKeysComponent,
    DataKeyConfigDialogComponent,
    DataKeyConfigComponent,
    DatasourceComponent,
    DatasourcesComponent,
    TargetDeviceComponent,
    EntityAliasSelectComponent,
    FilterSelectComponent,
    TimewindowStyleComponent,
    TimewindowStylePanelComponent,
    TimewindowConfigPanelComponent,
    WidgetSettingsComponent,
    WidgetSettingsCommonModule
  ]
})
export class WidgetConfigComponentsModule { }
