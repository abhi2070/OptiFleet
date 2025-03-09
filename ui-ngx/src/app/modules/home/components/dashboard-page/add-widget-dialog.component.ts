

import { ChangeDetectorRef, Component, Inject, OnInit, SkipSelf, ViewChild, ViewEncapsulation } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { DialogComponent } from '@app/shared/components/dialog.component';
import { Widget, WidgetConfigMode, widgetTypesData } from '@shared/models/widget.models';
import { Dashboard } from '@app/shared/models/dashboard.models';
import { IAliasController, IStateController } from '@core/api/widget-api.models';
import { WidgetConfigComponentData, WidgetInfo } from '@home/models/widget-component.models';
import { isDefined, isDefinedAndNotNull, isString } from '@core/utils';
import { TranslateService } from '@ngx-translate/core';
import { WidgetConfigComponent } from '@home/components/widget/widget-config.component';
import { BehaviorSubject } from 'rxjs';

export interface AddWidgetDialogData {
  dashboard: Dashboard;
  aliasController: IAliasController;
  stateController: IStateController;
  widget: Widget;
  widgetInfo: WidgetInfo;
}

@Component({
  selector: 'tb-add-widget-dialog',
  templateUrl: './add-widget-dialog.component.html',
  providers: [{provide: ErrorStateMatcher, useExisting: AddWidgetDialogComponent}],
  styleUrls: ['./add-widget-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AddWidgetDialogComponent extends DialogComponent<AddWidgetDialogComponent, Widget>
  implements OnInit, ErrorStateMatcher {

    widgetFormGroup: FormGroup;
    isLoading$ = new BehaviorSubject<boolean>(false);

    dashboard: Dashboard;
    aliasController: IAliasController;
    widget: Widget;

      private readonly initialWidgetConfigMode: WidgetConfigMode;
  
    submitted = false;
  
    constructor(protected store: Store<AppState>,
                protected router: Router,
                @Inject(MAT_DIALOG_DATA) public data: AddWidgetDialogData,
                @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
                public dialogRef: MatDialogRef<AddWidgetDialogComponent, Widget>,
                private fb: FormBuilder,
                private cdr: ChangeDetectorRef) {
      super(store, router, dialogRef);
  
      this.dashboard = this.data.dashboard;
      this.aliasController = this.data.aliasController;
      this.widget = this.data.widget;
  
      const widgetInfo = this.data.widgetInfo;
  
      const rawSettingsSchema = widgetInfo.typeSettingsSchema || widgetInfo.settingsSchema;
      const rawDataKeySettingsSchema = widgetInfo.typeDataKeySettingsSchema || widgetInfo.dataKeySettingsSchema;
      const rawLatestDataKeySettingsSchema = widgetInfo.typeLatestDataKeySettingsSchema || widgetInfo.latestDataKeySettingsSchema;
      const typeParameters = widgetInfo.typeParameters;
      const actionSources = widgetInfo.actionSources;
      const isDataEnabled = isDefined(widgetInfo.typeParameters) ? !widgetInfo.typeParameters.useCustomDatasources : true;
      let settingsSchema;
      if (!rawSettingsSchema || rawSettingsSchema === '') {
        settingsSchema = {};
      } else {
        settingsSchema = isString(rawSettingsSchema) ? JSON.parse(rawSettingsSchema) : rawSettingsSchema;
      }
      let dataKeySettingsSchema;
      if (!rawDataKeySettingsSchema || rawDataKeySettingsSchema === '') {
        dataKeySettingsSchema = {};
      } else {
        dataKeySettingsSchema = isString(rawDataKeySettingsSchema) ? JSON.parse(rawDataKeySettingsSchema) : rawDataKeySettingsSchema;
      }
      let latestDataKeySettingsSchema;
      if (!rawLatestDataKeySettingsSchema || rawLatestDataKeySettingsSchema === '') {
        latestDataKeySettingsSchema = {};
      } else {
        latestDataKeySettingsSchema = isString(rawLatestDataKeySettingsSchema) ?
          JSON.parse(rawLatestDataKeySettingsSchema) : rawLatestDataKeySettingsSchema;
      }
      const widgetConfig: WidgetConfigComponentData = {
        config: this.widget.config,
        layout: {},
        widgetType: this.widget.type,
        typeParameters,
        actionSources,
        isDataEnabled,
        settingsSchema,
        dataKeySettingsSchema,
        latestDataKeySettingsSchema,
        settingsDirective: widgetInfo.settingsDirective,
        dataKeySettingsDirective: widgetInfo.dataKeySettingsDirective,
        latestDataKeySettingsDirective: widgetInfo.latestDataKeySettingsDirective,
        widgetName: '',
        hasBasicMode: false,
        basicModeDirective: ''
      };
  
      this.widgetFormGroup = this.fb.group({
          widgetConfig: [widgetConfig, []]
        }
      );
    }
  
    ngOnInit(): void {
  this.isLoading$.subscribe(() => {
    this.cdr.detectChanges();
  });
    }
  
    isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
      const originalErrorState = this.errorStateMatcher.isErrorState(control, form);
      const customErrorState = !!(control && control.invalid && this.submitted);
      return originalErrorState || customErrorState;
    }
  
    helpLinkIdForWidgetType(): string {
      let link = 'widgetsConfig';
      if (this.widget && this.widget.type) {
        link = widgetTypesData.get(this.widget.type).configHelpLinkId;
      }
      return link;
    }
  
    cancel(): void {
      this.dialogRef.close(null);
    }
    
    add(): void {
      setTimeout(() => {
        this.submitted = true;
        const widgetConfig: WidgetConfigComponentData = this.widgetFormGroup.get('widgetConfig').value;
        this.widget.config = widgetConfig.config;
        this.widget.config.mobileOrder = widgetConfig.layout.mobileOrder;
        this.widget.config.mobileHeight = widgetConfig.layout.mobileHeight;
        this.widget.config.mobileHide = widgetConfig.layout.mobileHide;
        this.widget.config.desktopHide = widgetConfig.layout.desktopHide;
        this.dialogRef.close(this.widget);
      });
    }
}
