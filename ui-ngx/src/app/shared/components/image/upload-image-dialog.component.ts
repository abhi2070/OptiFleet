

import { Component, Inject, OnInit, SkipSelf } from '@angular/core';
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
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { ImageService } from '@core/http/image.service';
import { ImageResource, ImageResourceInfo, imageResourceType } from '@shared/models/resource.models';
import { getCurrentAuthState } from '@core/auth/auth.selectors';
import { forkJoin } from 'rxjs';
import { blobToBase64 } from '@core/utils';

export interface UploadImageDialogData {
  image?: ImageResourceInfo;
}

@Component({
  selector: 'tb-upload-image-dialog',
  templateUrl: './upload-image-dialog.component.html',
  providers: [{provide: ErrorStateMatcher, useExisting: UploadImageDialogComponent}],
  styleUrls: []
})
export class UploadImageDialogComponent extends
  DialogComponent<UploadImageDialogComponent, ImageResource> implements OnInit, ErrorStateMatcher {

  uploadImageFormGroup: UntypedFormGroup;

  uploadImage = true;

  submitted = false;

  maxResourceSize = getCurrentAuthState(this.store).maxResourceSize;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              private imageService: ImageService,
              @Inject(MAT_DIALOG_DATA) public data: UploadImageDialogData,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              public dialogRef: MatDialogRef<UploadImageDialogComponent, ImageResource>,
              public fb: UntypedFormBuilder) {
    super(store, router, dialogRef);
  }

  ngOnInit(): void {
    this.uploadImage = !this.data?.image;
    this.uploadImageFormGroup = this.fb.group({
      file: [this.data?.image?.link, [Validators.required]]
    });
    if (this.uploadImage) {
      this.uploadImageFormGroup.addControl('title', this.fb.control(null, [Validators.required]));
    }
  }

  imageFileNameChanged(fileName: string) {
    if (this.uploadImage) {
      const titleControl = this.uploadImageFormGroup.get('title');
      if (!titleControl.value || !titleControl.touched) {
        titleControl.setValue(fileName);
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

  upload(): void {
    this.submitted = true;
    const file: File = this.uploadImageFormGroup.get('file').value;
    if (this.uploadImage) {
      const title: string = this.uploadImageFormGroup.get('title').value;
      forkJoin([
        this.imageService.uploadImage(file, title),
        blobToBase64(file)
      ]).subscribe(([imageInfo, base64]) => {
        this.dialogRef.close(Object.assign(imageInfo, {base64}));
      });
    } else {
      const image = this.data.image;
      forkJoin([
        this.imageService.updateImage(imageResourceType(image), image.resourceKey, file),
        blobToBase64(file)
      ]).subscribe(([imageInfo, base64]) => {
        this.dialogRef.close(Object.assign(imageInfo, {base64}));
      });
    }
  }
}
