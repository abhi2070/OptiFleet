

import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'tb-threed-script-dialog',
  templateUrl: './threed-script-dialog.component.html',
  styleUrls: ['./../widget-settings.scss']
})
export class ThreedScriptDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ThreedScriptDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { fileName: string }) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
}