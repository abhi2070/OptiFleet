

import { Component, Inject, OnInit, SkipSelf } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DialogComponent } from '@app/shared/components/dialog.component';
import { ActionNotificationShow } from '@core/notification/notification.actions';

export interface ImportDialogData {
  importTitle: string;
  importFileLabel: string;
}

@Component({
  selector: 'tb-import-dialog',
  templateUrl: './import-dialog.component.html',
  providers: [{provide: ErrorStateMatcher, useExisting: ImportDialogComponent}],
  styleUrls: []
})
export class ImportDialogComponent extends DialogComponent<ImportDialogComponent>
  implements OnInit, ErrorStateMatcher {

  importTitle: string;
  importFileLabel: string;

  importFormGroup: UntypedFormGroup;

  submitted = false;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: ImportDialogData,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              public dialogRef: MatDialogRef<ImportDialogComponent>,
              private fb: UntypedFormBuilder) {
    super(store, router, dialogRef);
    this.importTitle = data.importTitle;
    this.importFileLabel = data.importFileLabel;

    this.importFormGroup = this.fb.group({
      jsonContent: [null, [Validators.required]]
    });
  }

  ngOnInit(): void {
  }

  isErrorState(control: UntypedFormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const originalErrorState = this.errorStateMatcher.isErrorState(control, form);
    const customErrorState = !!(control && control.invalid && this.submitted);
    return originalErrorState || customErrorState;
  }

  loadDataFromJsonContent(content: string): any {
    try {
      const importData = JSON.parse(content);
      return importData;
    } catch (err) {
      this.store.dispatch(new ActionNotificationShow({message: err.message, type: 'error'}));
      return null;
    }
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  importFromJson(): void {
    this.submitted = true;
    const importData = this.importFormGroup.get('jsonContent').value;
    this.dialogRef.close(importData);
  }
}
