

import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { getCurrentAuthUser } from '@core/auth/auth.selectors';
import { Authority } from '@shared/models/authority.enum';

export interface SaveWidgetTypeAsDialogResult {
  widgetName: string;
  widgetBundleId?: string;
}

@Component({
  selector: 'tb-save-widget-type-as-dialog',
  templateUrl: './save-widget-type-as-dialog.component.html',
  styleUrls: []
})
export class SaveWidgetTypeAsDialogComponent extends
  DialogComponent<SaveWidgetTypeAsDialogComponent, SaveWidgetTypeAsDialogResult> implements OnInit {

  saveWidgetTypeAsFormGroup: FormGroup;
  bundlesScope: string;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              public dialogRef: MatDialogRef<SaveWidgetTypeAsDialogComponent, SaveWidgetTypeAsDialogResult>,
              public fb: FormBuilder) {
    super(store, router, dialogRef);

    const authUser = getCurrentAuthUser(store);
    if (authUser.authority === Authority.TENANT_ADMIN) {
      this.bundlesScope = 'tenant';
    } else {
      this.bundlesScope = 'system';
    }
  }

  ngOnInit(): void {
    this.saveWidgetTypeAsFormGroup = this.fb.group({
      title: [null, [Validators.required]],
      widgetsBundle: [null]
    });
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  saveAs(): void {
    const widgetName: string = this.saveWidgetTypeAsFormGroup.get('title').value;
    const widgetBundleId: string = this.saveWidgetTypeAsFormGroup.get('widgetsBundle').value?.id?.id;
    const result: SaveWidgetTypeAsDialogResult = {
      widgetName,
      widgetBundleId
    };
    this.dialogRef.close(result);
  }
}
