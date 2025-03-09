import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/public-api';
import { Reports } from '@shared/models/reports.model';
import { DialogComponent } from '@app/shared/public-api';
import { EntityType } from '@shared/models/entity-type.models';
import { Store } from '@ngrx/store';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'tb-add-report-dialog',
  templateUrl: './add-report-dialog.component.html',
  styleUrls: ['./add-report-dialog.component.scss']
})
export class AddReportDialogComponent extends DialogComponent<AddReportDialogComponent, Reports> {

  addReportFormGroup: FormGroup;
  entityType = EntityType;

  constructor(protected store: Store<AppState>,
    protected router: Router,
    protected dialogRef: MatDialogRef<AddReportDialogComponent, Reports>,
    protected fb: FormBuilder) {
    super(store, router, dialogRef);

    this.addReportFormGroup = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(300)]]
    });
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  add(): void {

  }

}
