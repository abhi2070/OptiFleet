

import {
  AfterViewInit,
  Component,
  ComponentFactoryResolver,
  Inject,
  Injector,
  SkipSelf,
  ViewChild
} from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { UntypedFormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { AssetProfile } from '@shared/models/asset.models';
import { AssetProfileComponent } from '@home/components/profile/asset-profile.component';
import { AssetProfileService } from '@core/http/asset-profile.service';

export interface AssetProfileDialogData {
  assetProfile: AssetProfile;
  isAdd: boolean;
}

@Component({
  selector: 'tb-asset-profile-dialog',
  templateUrl: './asset-profile-dialog.component.html',
  providers: [{provide: ErrorStateMatcher, useExisting: AssetProfileDialogComponent}],
  styleUrls: []
})
export class AssetProfileDialogComponent extends
  DialogComponent<AssetProfileDialogComponent, AssetProfile> implements ErrorStateMatcher, AfterViewInit {

  isAdd: boolean;
  assetProfile: AssetProfile;

  submitted = false;

  @ViewChild('assetProfileComponent', {static: true}) assetProfileComponent: AssetProfileComponent;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: AssetProfileDialogData,
              public dialogRef: MatDialogRef<AssetProfileDialogComponent, AssetProfile>,
              private componentFactoryResolver: ComponentFactoryResolver,
              private injector: Injector,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              private assetProfileService: AssetProfileService) {
    super(store, router, dialogRef);
    this.isAdd = this.data.isAdd;
    this.assetProfile = this.data.assetProfile;
  }

  ngAfterViewInit(): void {
    if (this.isAdd) {
      setTimeout(() => {
        this.assetProfileComponent.entityForm.markAsDirty();
      }, 0);
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
    if (this.assetProfileComponent.entityForm.valid) {
      this.assetProfile = {...this.assetProfile, ...this.assetProfileComponent.entityFormValue()};
      this.assetProfileService.saveAssetProfile(this.assetProfile).subscribe(
        (assetProfile) => {
          this.dialogRef.close(assetProfile);
        }
      );
    }
  }
}
