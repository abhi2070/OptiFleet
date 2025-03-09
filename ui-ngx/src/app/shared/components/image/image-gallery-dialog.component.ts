

import { Component, Inject, OnInit, SkipSelf } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { ImageService } from '@core/http/image.service';
import { ImageResourceInfo, imageResourceType } from '@shared/models/resource.models';
import {
  UploadImageDialogComponent,
  UploadImageDialogData
} from '@shared/components/image/upload-image-dialog.component';
import { UrlHolder } from '@shared/pipe/image.pipe';
import { ImportExportService } from '@shared/import-export/import-export.service';
import { EmbedImageDialogComponent, EmbedImageDialogData } from '@shared/components/image/embed-image-dialog.component';

@Component({
  selector: 'tb-image-gallery-dialog',
  templateUrl: './image-gallery-dialog.component.html',
  styleUrls: ['./image-gallery-dialog.component.scss']
})
export class ImageGalleryDialogComponent extends
  DialogComponent<ImageGalleryDialogComponent, ImageResourceInfo> implements OnInit {

  constructor(protected store: Store<AppState>,
              protected router: Router,
              private imageService: ImageService,
              private dialog: MatDialog,
              public dialogRef: MatDialogRef<ImageGalleryDialogComponent, ImageResourceInfo>) {
    super(store, router, dialogRef);
  }

  ngOnInit(): void {
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  imageSelected(image: ImageResourceInfo): void {
    this.dialogRef.close(image);
  }

}
