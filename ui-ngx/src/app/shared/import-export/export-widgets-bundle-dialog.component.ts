

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { DialogComponent } from '@app/shared/components/dialog.component';
import { WidgetsBundle } from '@shared/models/widgets-bundle.model';

export interface ExportWidgetsBundleDialogData {
  widgetsBundle: WidgetsBundle;
  includeBundleWidgetsInExport: boolean;
}

export interface ExportWidgetsBundleDialogResult {
  exportWidgets: boolean;
}

@Component({
  selector: 'tb-export-widgets-bundle-dialog',
  templateUrl: './export-widgets-bundle-dialog.component.html',
  providers: [],
  styleUrls: []
})
export class ExportWidgetsBundleDialogComponent extends DialogComponent<ExportWidgetsBundleDialogComponent, ExportWidgetsBundleDialogResult>
  implements OnInit {

  widgetsBundle: WidgetsBundle;

  exportWidgetsFormControl = new FormControl(false);

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: ExportWidgetsBundleDialogData,
              public dialogRef: MatDialogRef<ExportWidgetsBundleDialogComponent, ExportWidgetsBundleDialogResult>) {
    super(store, router, dialogRef);
    this.widgetsBundle = data.widgetsBundle;
    if (data.includeBundleWidgetsInExport) {
      this.exportWidgetsFormControl.patchValue(data.includeBundleWidgetsInExport, {emitEvent: false});
    }
  }

  ngOnInit(): void {
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  export(): void {
    this.dialogRef.close({
      exportWidgets: this.exportWidgetsFormControl.value
    });
  }
}
