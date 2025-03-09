

import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { FormBuilder, FormControl } from '@angular/forms';

export interface GatewayRemoteConfigurationDialogData {
  gatewayName: string;
}

@Component({
  selector: 'gateway-remote-configuration-dialog',
  templateUrl: './gateway-remote-configuration-dialog.html'
})

export class GatewayRemoteConfigurationDialogComponent extends
  DialogComponent<GatewayRemoteConfigurationDialogComponent, boolean>{

  gatewayName: string;
  gatewayControl: FormControl;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: GatewayRemoteConfigurationDialogData,
              public dialogRef: MatDialogRef<GatewayRemoteConfigurationDialogComponent, boolean>,
              private fb: FormBuilder) {
    super(store, router, dialogRef);
    this.gatewayName = this.data.gatewayName;
    this.gatewayControl = this.fb.control('');
  }

  close(): void {
    this.dialogRef.close();
  }

  turnOff(): void {
    this.dialogRef.close(true);
  }
}
