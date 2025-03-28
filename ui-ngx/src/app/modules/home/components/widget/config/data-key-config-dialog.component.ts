

import { Component, Inject, OnInit, SkipSelf, ViewChild } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import {
  FormGroupDirective,
  NgForm,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import { DialogComponent } from '@shared/components/dialog.component';
import { DataKey, DataKeyConfigMode, Widget, widgetType } from '@shared/models/widget.models';
import { DataKeysCallbacks } from './data-keys.component.models';
import { DataKeyConfigComponent } from '@home/components/widget/config/data-key-config.component';
import { Dashboard } from '@shared/models/dashboard.models';
import { IAliasController } from '@core/api/widget-api.models';
import { ToggleHeaderOption } from '@shared/components/toggle-header.component';
import { TranslateService } from '@ngx-translate/core';

export interface DataKeyConfigDialogData {
  dataKey: DataKey;
  dataKeyConfigMode?: DataKeyConfigMode;
  dataKeySettingsSchema: any;
  dataKeySettingsDirective: string;
  dashboard: Dashboard;
  aliasController: IAliasController;
  widget: Widget;
  widgetType: widgetType;
  deviceId?: string;
  entityAliasId?: string;
  showPostProcessing?: boolean;
  callbacks?: DataKeysCallbacks;
  hideDataKeyName?: boolean;
  hideDataKeyLabel?: boolean;
  hideDataKeyColor?: boolean;
  hideDataKeyUnits?: boolean;
  hideDataKeyDecimals?: boolean;
}

@Component({
  selector: 'tb-data-key-config-dialog',
  templateUrl: './data-key-config-dialog.component.html',
  providers: [{provide: ErrorStateMatcher, useExisting: DataKeyConfigDialogComponent}],
  styleUrls: ['./data-key-config-dialog.component.scss']
})
export class DataKeyConfigDialogComponent extends DialogComponent<DataKeyConfigDialogComponent, DataKey>
  implements OnInit, ErrorStateMatcher {

  @ViewChild('dataKeyConfig', {static: true}) dataKeyConfig: DataKeyConfigComponent;

  hasAdvanced = false;

  dataKeyConfigHeaderOptions: ToggleHeaderOption[];

  dataKeyConfigMode: DataKeyConfigMode = DataKeyConfigMode.general;

  dataKeyFormGroup: UntypedFormGroup;

  submitted = false;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: DataKeyConfigDialogData,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              public dialogRef: MatDialogRef<DataKeyConfigDialogComponent, DataKey>,
              private translate: TranslateService,
              public fb: UntypedFormBuilder) {
    super(store, router, dialogRef);
  }

  ngOnInit(): void {
    this.dataKeyFormGroup = this.fb.group({
      dataKey: [this.data.dataKey, [Validators.required]]
    });
    if (this.data.dataKeySettingsSchema && this.data.dataKeySettingsSchema.schema ||
      this.data.dataKeySettingsDirective && this.data.dataKeySettingsDirective.length) {
      this.hasAdvanced = true;
      this.dataKeyConfigHeaderOptions = [
        {
          name: this.translate.instant('datakey.general'),
          value: DataKeyConfigMode.general
        },
        {
          name: this.translate.instant('datakey.advanced'),
          value: DataKeyConfigMode.advanced
        }
      ];
      if (this.data.dataKeyConfigMode) {
        this.dataKeyConfigMode = this.data.dataKeyConfigMode;
      }
    }
  }

  isErrorState(control: UntypedFormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const originalErrorState = this.errorStateMatcher.isErrorState(control, form);
    const customErrorState = !!(control && control.invalid && this.submitted);
    return originalErrorState || customErrorState;
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  save(): void {
    this.submitted = true;
    this.dataKeyConfig.validateOnSubmit();
    if (this.dataKeyFormGroup.valid) {
      const dataKey: DataKey = this.dataKeyFormGroup.get('dataKey').value;
      this.dialogRef.close(dataKey);
    }
  }
}
