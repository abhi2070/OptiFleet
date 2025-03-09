

import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface ErrorAlertDialogData {
  title: string;
  message: string;
  error: any;
  ok: string;
}

@Component({
  selector: 'tb-error-alert-dialog',
  templateUrl: './error-alert-dialog.component.html',
  styleUrls: ['./error-alert-dialog.component.scss']
})
export class ErrorAlertDialogComponent {

  title: string;
  message: string;
  errorMessage: string;
  errorDetails?: string;

  constructor(public dialogRef: MatDialogRef<ErrorAlertDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: ErrorAlertDialogData) {
    this.title = this.data.title;
    this.message = this.data.message;
    this.errorMessage = this.data.error.message ? this.data.error.message : JSON.stringify(this.data.error);
    if (this.data.error.stack) {
      this.errorDetails = this.data.error.stack.replaceAll('\n', '<br/>');
    }
  }

}
