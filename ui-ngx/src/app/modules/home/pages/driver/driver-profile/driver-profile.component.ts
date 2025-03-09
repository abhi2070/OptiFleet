import { Component, Input, OnDestroy, OnInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Subject } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import { DriverService } from '@app/core/http/driver.service';
import { Driver } from '@app/shared/models/driver.model';
import { EntityId } from '@app/shared/public-api';

interface VerificationStatus {
  success: boolean;
  message: string;
}

@Component({
  selector: 'tb-driver-profile',
  templateUrl: './driver-profile.component.html',
  styleUrls: ['./driver-profile.component.scss']
})
export class DriverProfileComponent implements OnInit, OnDestroy {
  private readonly MAX_PDF_SIZE = 200 * 1024;
  private readonly destroy$ = new Subject<void>();

  pdfFile: File | null = null;
  pdfPreview: SafeUrl | null = null;
  fileName: string | null = null;
  error: string | null = null;
  originalPdfUrl: string | null = null;

  isUploading = false;
  isVerifying = false;
  isLoading = false;
  verificationStatus: VerificationStatus | null = null;

  private activeValue = false;
  private dirtyValue = false;
  private entityIdValue: EntityId;
  private driverId: string;

  @Input()
  set active(active: boolean) {
    if (this.activeValue !== active) {
      this.activeValue = active;
      if (this.activeValue && this.dirtyValue) {
        this.dirtyValue = false;
        this.loadDriverDocument();
      }
    }
  }

  @Input()
  set entityId(entityId: EntityId) {
    if (this.entityIdValue !== entityId) {
      this.entityIdValue = entityId;
      this.driverId = entityId.id;
      if (!this.activeValue) {
        this.dirtyValue = true;
      } else {
        this.loadDriverDocument();
      }
    }
  }

  constructor(
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer,
    private driverService: DriverService
  ) {}

  ngOnInit(): void {
    this.loadDriverDocument();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDriverDocument(): void {
    if (!this.driverId) return;

    this.isLoading = true;
    this.resetState();
    this.cdr.detectChanges();

    this.driverService.getDriverInfo(this.driverId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (driver: Driver) => {
          if (driver.drivingLicenseDocument) {
            this.originalPdfUrl = driver.drivingLicenseDocument;
            this.loadAndSetDocument(driver.drivingLicenseDocument);
          }
        },
        error: (error) => {
          this.showError('Error loading driver document');
        }
      });
  }

  private resetState(): void {
    this.pdfFile = null;
    this.pdfPreview = null;
    this.fileName = null;
    this.error = null;
    this.verificationStatus = null;
  }

  private loadAndSetDocument(base64Document: string): void {
    try {
      if (!base64Document) {
        throw new Error('No document data received');
      }
      const cleanBase64 = this.validateBase64String(base64Document);

      const binaryString = window.atob(cleanBase64);
      const bytes = new Uint8Array(binaryString.length);
      for (let i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i);
      }
      const blob = new Blob([bytes], { type: 'application/pdf' });

      const objectUrl = URL.createObjectURL(blob);
      this.pdfPreview = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
      this.fileName = "Driver's License";
      this.originalPdfUrl = base64Document;
      this.cdr.detectChanges();

    } catch (error) {
      this.showError('Error loading document');
    }
  }

  private validateBase64String(base64String: string): string {
    const cleanedString = base64String.trim().replace(/[\s\r\n]+/g, '');
    if (!cleanedString) throw new Error('Empty base64 string');

    const base64Data = cleanedString.includes(',') ?
      cleanedString.split(',')[1] : cleanedString;

    const base64Regex = /^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$/;
    if (!base64Regex.test(base64Data)) {
      throw new Error('Invalid base64 format');
    }

    return base64Data;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];
    this.error = null;

    if (file.type !== 'application/pdf') {
      this.error = 'Only PDF format is allowed';
      this.cdr.detectChanges();
      return;
    }

    if (file.size > this.MAX_PDF_SIZE) {
      this.error = `File size must be less than ${this.MAX_PDF_SIZE / 1024}KB`;
      this.cdr.detectChanges();
      return;
    }

    this.pdfFile = file;
    this.fileName = file.name;
    this.createPreview(file);
  }

  private createPreview(file: File): void {
    try {
      const objectUrl = URL.createObjectURL(file);
      this.pdfPreview = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
      this.cdr.detectChanges();
    } catch (error) {
      this.showError('Error previewing PDF');
    }
  }

  uploadFile(): void {
    if (!this.pdfFile || !this.driverId) {
      this.showError('Please select a file to upload');
      return;
    }

    this.isUploading = true;
    this.cdr.detectChanges();

    this.driverService.uploadDriverLicenseDocument(this.driverId, this.pdfFile)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isUploading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (driver: Driver) => {
          this.showSuccess('License document uploaded successfully');
          if (driver.drivingLicenseDocument) {
            this.loadAndSetDocument(driver.drivingLicenseDocument);
          }
        },
        error: () => {
          this.showError('Error uploading document');
        }
      });
  }


  verifyDocument(): void {
    if (!this.pdfPreview || !this.driverId) {
      this.showError('Please upload a document first');
      return;
    }

    this.isVerifying = true;
    this.verificationStatus = null;
    this.cdr.detectChanges();

    this.driverService.verifyDriver(this.driverId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isVerifying = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (isVerified: boolean) => {
          this.verificationStatus = {
            success: isVerified,
            message: isVerified
              ? 'Document verified successfully'
              : 'Document verification failed'
          };

          setTimeout(() => {
            this.verificationStatus = null;
            this.cdr.detectChanges();
          }, 3000);
        },
        error: () => {
          this.showError('Error verifying document');
        }
      });
  }


  uploadButtonEnabled(): boolean {
    return !!this.pdfFile &&
           !this.isUploading &&
           !this.isVerifying;
  }

 isVerifyButtonEnabled(): boolean {
  return !!this.pdfFile &&
         !this.isVerifying && 
         !this.verificationStatus?.success;
}

  removeFile(): void {
      this.resetState();
    this.cdr.detectChanges();
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar'],
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: ['error-snackbar'],
    });
  }
  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  @HostListener('dragover', ['$event'])
  onDragOver(event: DragEvent): void {
  event.preventDefault();
  event.stopPropagation();
}

@HostListener('drop', ['$event'])
onDrop(event: DragEvent): void {
  event.preventDefault();
  event.stopPropagation();

  const files = event.dataTransfer?.files;
  if (files && files.length > 0) {
    this.handleDroppedFile(files[0]);
  }
}

private handleDroppedFile(file: File): void {
  this.error = null;

  if (file.type !== 'application/pdf') {
    this.error = 'Only PDF format is allowed';
    this.cdr.detectChanges();
    return;
  }

  if (file.size > this.MAX_PDF_SIZE) {
    this.error = `File size must be less than ${this.MAX_PDF_SIZE / 1024}KB`;
    this.cdr.detectChanges();
    return;
  }

  this.pdfFile = file;
  this.fileName = file.name;
  this.createPreview(file);
}
}
