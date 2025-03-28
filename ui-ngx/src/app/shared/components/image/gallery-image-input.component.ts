

import { ChangeDetectorRef, Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR, } from '@angular/forms';
import { coerceBoolean } from '@shared/decorators/coercion';
import {
  extractParamsFromImageResourceUrl,
  ImageResourceInfo,
  isBase64DataImageUrl,
  isImageResourceUrl,
  prependTbImagePrefix,
  removeTbImagePrefix
} from '@shared/models/resource.models';
import { ImageService } from '@core/http/image.service';
import { MatDialog } from '@angular/material/dialog';
import { ImageGalleryDialogComponent } from '@shared/components/image/image-gallery-dialog.component';

export enum ImageLinkType {
  none = 'none',
  base64 = 'base64',
  external = 'external',
  resource = 'resource'
}

@Component({
  selector: 'tb-gallery-image-input',
  templateUrl: './gallery-image-input.component.html',
  styleUrls: ['./gallery-image-input.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => GalleryImageInputComponent),
      multi: true
    }
  ]
})
export class GalleryImageInputComponent extends PageComponent implements OnInit, OnDestroy, ControlValueAccessor {

  @Input()
  label: string;

  @Input()
  @coerceBoolean()
  required = false;

  @Input()
  disabled: boolean;

  imageUrl: string;

  imageResource: ImageResourceInfo;

  loadingImageResource = false;

  ImageLinkType = ImageLinkType;

  linkType: ImageLinkType = ImageLinkType.none;

  externalLinkControl = new FormControl(null);

  private propagateChange = null;

  constructor(protected store: Store<AppState>,
              private imageService: ImageService,
              private dialog: MatDialog,
              private cd: ChangeDetectorRef) {
    super(store);
  }

  ngOnInit() {
    this.externalLinkControl.valueChanges.subscribe((value) => {
      if (this.linkType === ImageLinkType.external) {
        this.updateModel(value);
      }
    });
  }

  ngOnDestroy() {
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.detectLinkType();
      this.externalLinkControl.disable({emitEvent: false});
    } else {
      this.externalLinkControl.enable({emitEvent: false});
    }
  }

  writeValue(value: string): void {
    value = removeTbImagePrefix(value);
    if (this.imageUrl !== value) {
      this.reset();
      this.imageUrl = value;
      this.detectLinkType();
      if (this.linkType === ImageLinkType.resource) {
        const params = extractParamsFromImageResourceUrl(this.imageUrl);
        this.loadingImageResource = true;
        this.imageService.getImageInfo(params.type, params.key, {ignoreLoading: true, ignoreErrors: true}).subscribe(
          {
            next: (res) => {
              this.imageResource = res;
              this.loadingImageResource = false;
              this.cd.markForCheck();
            },
            error: () => {
              this.reset();
              this.loadingImageResource = false;
              this.cd.markForCheck();
            }
          }
        );
      } else if (this.linkType === ImageLinkType.base64) {
        this.cd.markForCheck();
      } else if (this.linkType === ImageLinkType.external) {
        this.externalLinkControl.setValue(this.imageUrl, {emitEvent: false});
        this.cd.markForCheck();
      }
    }
  }

  private detectLinkType() {
    if (this.imageUrl) {
      if (isImageResourceUrl(this.imageUrl)) {
        this.linkType = ImageLinkType.resource;
      } else if (isBase64DataImageUrl(this.imageUrl)) {
        this.linkType = ImageLinkType.base64;
      } else {
        this.linkType = ImageLinkType.external;
      }
    } else {
      this.linkType = ImageLinkType.none;
    }
  }

  private updateModel(value: string) {
    this.cd.markForCheck();
    if (this.imageUrl !== value) {
      this.imageUrl = value;
      this.propagateChange(prependTbImagePrefix(this.imageUrl));
    }
  }

  private reset() {
    this.linkType = ImageLinkType.none;
    this.imageResource = null;
    this.externalLinkControl.setValue(null, {emitEvent: false});
  }

  clearImage() {
    this.reset();
    this.updateModel(null);
  }

  setLink($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.linkType = ImageLinkType.external;
  }

  openGallery($event: Event): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<ImageGalleryDialogComponent, any,
      ImageResourceInfo>(ImageGalleryDialogComponent, {
        autoFocus: false,
        disableClose: false,
        panelClass: ['tb-dialog', 'tb-fullscreen-dialog']
    }).afterClosed().subscribe((image) => {
      if (image) {
        this.linkType = ImageLinkType.resource;
        this.imageResource = image;
        this.updateModel(image.link);
      }
    });
  }

}
