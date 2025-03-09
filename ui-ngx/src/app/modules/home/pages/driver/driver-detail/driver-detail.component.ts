import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { AppState } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DriverInfo } from '@app/shared/models/driver.model';
import { VehicleInfo } from '@app/shared/models/vehicle.model';
import { EntityType } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { finalize, Subject, takeUntil } from 'rxjs';
import { DriverService } from '@app/core/http/driver.service';

@Component({
  selector: 'tb-driver-detail',
  templateUrl: './driver-detail.component.html',
  styleUrls: ['./driver-detail.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DriverDetailComponent extends EntityComponent<DriverInfo> implements OnInit, OnDestroy {
  @Input() editModeToggled: EventEmitter<void>;

  readonly DEFAULT_PROFILE_PHOTO = 'assets/images/default-profile.png';
  private readonly VALID_IMAGE_TYPES = ['image/jpeg', 'image/jpg'];
  private readonly MAX_PHOTO_SIZE = 50 * 1024; 
  private readonly MIN_IMAGE_DIMENSION = 100; 
  private readonly destroy$ = new Subject<void>();

  profilePhoto: SafeUrl | null = null;
  originalProfilePhotoUrl: string | null = null;
  profilePhotoFile: File | null = null;
  photoError: string | null = null;
  isSavingPhoto: boolean = false;

  entityForm: FormGroup;
  entityType = EntityType;
  genderOptions = ['Male', 'Female', 'Other'];
  driverStatuses = [
    { value: 'AVAILABLE', label: 'Available' },
    { value: 'ON_TRIP', label: 'On Trip' },
    { value: 'ON_LEAVE', label: 'On Leave' },
    { value: 'DISCONTINUED', label: 'Discontinued' }
  ];
  hide = true;
  driverScope: 'tenant';

  constructor(
    protected store: Store<AppState>,
    private dialog: MatDialog,
    protected translate: TranslateService,
    protected router: Router,
    public fb: UntypedFormBuilder,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private driverService: DriverService,
    @Inject('entity') protected entityValue: DriverInfo,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<DriverInfo>,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  ngOnInit(): void {
    this.driverScope = this.entitiesTableConfig.componentsData.driverScope;
    super.ngOnInit();
    this.resetPhotoState();
    if (this.entity?.profilePhoto) {
      this.loadAndSetProfilePhoto(this.entity.profilePhoto);
    } else {
      this.setDefaultProfilePhoto();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private resetPhotoState(): void {
    this.profilePhoto = null;
    this.originalProfilePhotoUrl = null;
    this.profilePhotoFile = null;
    this.photoError = null;
    this.cd.detectChanges();
  }

  updateForm(entity: DriverInfo) {
    this.resetPhotoState();
    this.entityForm.patchValue({
      name: entity.name,
      gender: entity.gender,
      dateOfBirth: this.convertMillisToDate(entity.dateOfBirth),
      serviceStartDate: this.convertMillisToDate(entity.serviceStartDate),
      drivingLicenseNumber: entity.drivingLicenseNumber,
      drivingLicenseValidity: this.convertMillisToDate(entity.drivingLicenseValidity),
      status: entity.status,
      profilePhoto: entity.profilePhoto,
    });
    if (entity.profilePhoto) {
      this.loadAndSetProfilePhoto(entity.profilePhoto);
    } else {
      this.setDefaultProfilePhoto();
    }
  }


  buildForm(entity: DriverInfo): FormGroup {
    return this.fb.group({
      name: [entity ? entity.name : '', [Validators.required]],
      gender: [entity ? entity.gender : '', [Validators.required]],
      dateOfBirth: [entity ? this.convertMillisToDate(entity.dateOfBirth) : '', [Validators.required]],
      serviceStartDate: [entity ? this.convertMillisToDate(entity.serviceStartDate) : '', [Validators.required]],
      drivingLicenseNumber: [entity ? entity.drivingLicenseNumber : '', [Validators.required]],
      drivingLicenseValidity: [entity ? this.convertMillisToDate(entity.drivingLicenseValidity) : '', [Validators.required]],
      status: [entity ? entity.status : 'AVAILABLE', [Validators.required]],
      profilePhoto: [entity ? entity.profilePhoto : null],

    });
  }

  private validateImageFile(file: File): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.VALID_IMAGE_TYPES.includes(file.type)) {
        return reject(new Error('Only JPEG/JPG format is allowed'));
      }

      if (file.size > this.MAX_PHOTO_SIZE) {
        return reject(new Error(`File size must be less than ${this.MAX_PHOTO_SIZE / 1024}KB`));
      }

      const reader = new FileReader();
      reader.onload = (e: ProgressEvent<FileReader>) => {
        const img = new Image();
        img.onload = () => {
          if (img.width < this.MIN_IMAGE_DIMENSION || img.height < this.MIN_IMAGE_DIMENSION) {
            return reject(new Error(`Image dimensions must be at least ${this.MIN_IMAGE_DIMENSION}x${this.MIN_IMAGE_DIMENSION} pixels`));
          }
          resolve();
        };
        img.onerror = () => reject(new Error('Invalid image file'));
        img.src = e.target?.result as string;
      };
      reader.onerror = () => reject(new Error('Error reading file'));
      reader.readAsDataURL(file);
    });
  }

  private async loadAndSetProfilePhoto(photoData: string): Promise<void> {
    if (!photoData) {
      this.setDefaultProfilePhoto();
      return;
    }
    try {
      this.profilePhoto = null;
      this.cd.detectChanges();

      let safeUrl: SafeUrl;

      if (photoData.startsWith('data:image')) {
        safeUrl = this.sanitizer.bypassSecurityTrustUrl(photoData);
      } else {
        const dataUrl = `data:image/jpeg;base64,${photoData}`;
        await this.validateImageDataUrl(dataUrl);
        safeUrl = this.sanitizer.bypassSecurityTrustUrl(dataUrl);
      }
      this.profilePhoto = safeUrl;
      this.originalProfilePhotoUrl = photoData;
      this.cd.detectChanges();
    } catch (error) {
      this.setDefaultProfilePhoto();
    }
  }

  private setDefaultProfilePhoto(): void {
    this.profilePhoto = null;
    this.originalProfilePhotoUrl = null;
    this.cd.detectChanges();

    this.profilePhoto = this.sanitizer.bypassSecurityTrustUrl(this.DEFAULT_PROFILE_PHOTO);
    this.cd.detectChanges();
  }
  saveProfilePhoto(): void {
    if (!this.isEdit) return;

    if (!this.profilePhotoFile || !this.entity?.id?.id) {
      this.store.dispatch(new ActionNotificationShow({
        message: this.translate.instant('driver.no-photo-selected'),
        type: 'error'
      }));
      return;
    }
    this.isSavingPhoto = true;
    this.cd.detectChanges();

    this.driverService.uploadDriverPhotoDocument(this.entity.id.id, this.profilePhotoFile)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isSavingPhoto = false;
          this.cd.detectChanges();
        })
      )
      .subscribe({
        next: (driver: DriverInfo) => {
          this.store.dispatch(new ActionNotificationShow({
            message: this.translate.instant('driver.profile-photo-saved'),
            type: 'success'
          }));
          if (driver.profilePhoto) {
            this.loadAndSetProfilePhoto(driver.profilePhoto);
            this.profilePhotoFile = null;
          }
        },
        error: (error) => {
          console.error('Error uploading profile photo:', error);
          this.store.dispatch(new ActionNotificationShow({
            message: this.translate.instant('driver.profile-photo-save-error'),
            type: 'error'
          }));
        }
      });
    }

    cancelPhotoChanges(): void {
      this.profilePhotoFile = null;
      this.photoError = null;

      if (this.originalProfilePhotoUrl) {
        this.loadAndSetProfilePhoto(this.originalProfilePhotoUrl);
      } else {
        this.removeProfilePhoto();
      }
    }

    removeProfilePhoto(): void {
      this.profilePhoto = null;
      this.profilePhotoFile = null;
      this.photoError = null;
      this.originalProfilePhotoUrl = null;
      this.setDefaultProfilePhoto();

      if (this.entityForm) {
        this.entityForm.patchValue({
          profilePhoto: null
        });
      }
      this.cd.detectChanges();
    }

    async onProfilePhotoSelected(event: Event): Promise<void> {
      if (!this.isEdit) return;
      const input = event.target as HTMLInputElement;
      if (!input.files?.length) return;

      const file = input.files[0];

      try {
        await this.validateImageFile(file);
        this.photoError = null;
        this.profilePhotoFile = file;
        this.profilePhoto = this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(file));
        this.cd.detectChanges();
      } catch (error) {
        this.photoError = error.message;
        input.value = '';
        this.cd.detectChanges();
      }
    }


  revertToOriginalPhoto(): void {
    if (this.originalProfilePhotoUrl) {
      this.loadAndSetProfilePhoto(this.originalProfilePhotoUrl);
      this.profilePhotoFile = null;
      this.photoError = null;
    }
  }

  private async validateImageDataUrl(dataUrl: string): Promise<void> {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.onload = () => resolve();
      img.onerror = () => reject(new Error('Invalid image data'));
      img.src = dataUrl;
    });
  }

  prepareFormValue(): DriverInfo {
    const formValue = this.entityForm.value;
    return {
      ...formValue,
      dateOfBirth: this.convertDateToMillis(formValue.dateOfBirth),
      serviceStartDate: this.convertDateToMillis(formValue.serviceStartDate),
      drivingLicenseValidity: this.convertDateToMillis(formValue.drivingLicenseValidity),
      profilePhoto: formValue.profilePhoto
    } as DriverInfo;
  }

  private convertMillisToDate(millis: number): Date | null {
    return millis ? new Date(millis) : null;
  }

  private convertDateToMillis(date: Date): number | null {
    return date ? date.getTime() : null;
  }

  onDriverIdCopied($event: any) {
    this.store.dispatch(new ActionNotificationShow({
      message: this.translate.instant('driver.idCopiedMessage'),
      type: 'success',
      duration: 750,
      verticalPosition: 'bottom',
      horizontalPosition: 'right'
    }));
  }

  hideDelete() {
    return this.entitiesTableConfig ? !this.entitiesTableConfig.deleteEnabled(this.entity) : false;
  }
}
