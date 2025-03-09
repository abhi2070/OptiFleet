import { Component, Input, OnDestroy, OnInit, ChangeDetectorRef } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DomSanitizer, SafeResourceUrl, SafeUrl } from '@angular/platform-browser';
import { Subject } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import { VehicleService } from '@app/core/http/vehicle.service';
import { Vehicle } from '@app/shared/models/vehicle.model';
import { EntityId } from '@app/shared/public-api';

interface DocumentType {
  key: string;
  title: string;
  icon: string;
  isExpanded?: boolean;
}

interface DocumentInfo {
  pdfFile: File | null;
  pdfPreview: SafeUrl | null;
  error: string | null;
  originalUrl: string | null;
}

interface VerificationStatus {
  success: boolean;
  message: string;
}

@Component({
  selector: 'tb-vehicle-profile',
  templateUrl: './vehicle-profile.component.html',
  styleUrls: ['./vehicle-profile.component.scss']
})
export class VehicleProfileComponent implements OnInit, OnDestroy {
  private readonly MAX_PDF_SIZE = 200 * 1024;
  private readonly destroy$ = new Subject<void>();

  documentTypes: DocumentType[] = [
    { key: 'rc', title: 'Registration Certificate', icon: 'airport_shuttle' },
    { key: 'insurance', title: 'Insurance Certificate', icon: 'local_hospital' },
    { key: 'puc', title: 'Pollution Under Control', icon: 'eco' },
    { key: 'permits', title: 'Required Permits', icon: 'assignment' }
  ];

  documents: { [key: string]: DocumentInfo } = {};
  preview: SafeUrl | null = null;

  isUploading = false;
  isLoading = false;
  isVerifying = false;
  verificationStatus: VerificationStatus | null = null;

  private activeValue = false;
  private dirtyValue = false;
  private entityIdValue: EntityId;
  private vehicleId: string;

  @Input()
  set active(active: boolean) {
    if (this.activeValue !== active) {
      this.activeValue = active;
      if (this.activeValue && this.dirtyValue) {
        this.dirtyValue = false;
        this.loadVehicleDocuments();
      }
    }
  }

  @Input()
  set entityId(entityId: EntityId) {
    if (this.entityIdValue !== entityId) {
      this.entityIdValue = entityId;
      this.vehicleId = entityId.id;
      if (!this.activeValue) {
        this.dirtyValue = true;
      } else {
        this.loadVehicleDocuments();
      }
    }
  }

  constructor(
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer,
    private vehicleService: VehicleService
  ) {
    this.initializeDocuments();
  }

  ngOnInit(): void {
    this.loadVehicleDocuments();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeDocuments(): void {
    this.documentTypes.forEach(doc => {
      this.documents[doc.key] = {
        pdfFile: null,
        pdfPreview: null,
        error: null,
        originalUrl: null
      };
    });
  }

  loadVehicleDocuments(): void {
    if (!this.vehicleId) return;

    this.isLoading = true;
    this.resetState();

    this.vehicleService.getVehicle(this.vehicleId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (vehicle: Vehicle) => {
          this.loadAndSetDocument('rc', vehicle.registrationCertificate);
          this.loadAndSetDocument('insurance', vehicle.insuranceCertificate);
          this.loadAndSetDocument('puc', vehicle.pucCertificate);
          this.loadAndSetDocument('permits', vehicle.requiredPermits);
        },
        error: () => {
          this.showError('Error loading vehicle documents');
        }
      });
  }

  private resetState(): void {
    Object.keys(this.documents).forEach(key => {
      this.documents[key].pdfFile = null;
      this.documents[key].pdfPreview = null;
      this.documents[key].error = null;
      this.documents[key].originalUrl = null;
    });
  }

  private loadAndSetDocument(documentType: string, base64Document: string): void {
    try {
      if (!base64Document) return;

      const cleanBase64 = this.validateBase64String(base64Document);

      const binaryString = window.atob(cleanBase64);
      const bytes = new Uint8Array(binaryString.length);
      for (let i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i);
      }
      const blob = new Blob([bytes], { type: 'application/pdf' });

      const objectUrl = URL.createObjectURL(blob);
      this.documents[documentType].pdfPreview = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
      this.documents[documentType].originalUrl = base64Document;
      this.cdr.detectChanges();

    } catch (error) {
      this.showError(`Error loading ${documentType} document`);
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

  onFileSelected(event: Event, documentType: string): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];
    this.processFile(file, documentType);
  }

  onDropFile(event: DragEvent, documentType: string): void {
    event.preventDefault();
    event.stopPropagation();
    const file = event.dataTransfer?.files[0];
    this.processFile(file, documentType);
  }

  isVerifyButtonEnabled(): boolean {
    return Object.values(this.documents).some(
      doc => doc.pdfFile !== null &&
             (!doc.originalUrl || doc.pdfFile !== null)
    ) && !this.isVerifying;
  }

  verifyDocument(): void {
    if (!this.vehicleId) {
      this.showError('No vehicle selected');
      return;
    }

    this.isVerifying = true;
    this.verificationStatus = null;
    this.cdr.detectChanges();

    this.vehicleService.verifyVehicle(this.vehicleId)
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
            message: isVerified ?
              'Document verified successfully' :
              'Document verification failed'
          };

          if (isVerified) {
            Object.keys(this.documents).forEach(key => {
              if (this.documents[key].pdfFile) {
                this.documents[key].pdfFile = null;
              }
            });
          }

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

  private processFile(file: File, documentType: string): void {
    this.documents[documentType].error = null;
    if (!file) return;

    if (file.type !== 'application/pdf') {
      this.documents[documentType].error = 'Only PDF files are allowed';
      this.cdr.detectChanges();
      return;
    }

    if (file.size > this.MAX_PDF_SIZE) {
      this.documents[documentType].error = `File size must be less than ${this.MAX_PDF_SIZE / 1024}KB`;
      this.cdr.detectChanges();
      return;
    }

    this.documents[documentType].pdfFile = file;
    this.createPreview(file, documentType);
  }

  private createPreview(file: File, documentType: string): void {
    try {
      const objectUrl = URL.createObjectURL(file);
      this.documents[documentType].pdfPreview = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
      this.cdr.detectChanges();
    } catch (error) {
      this.showError(`Error previewing ${documentType} PDF`);
    }
  }

  hasDocumentsToVerify(): boolean {
    return Object.values(this.documents).some(doc => doc.pdfPreview !== null);
  }

  uploadAllDocuments(): void {
    if (!this.vehicleId) {
      this.showError('Please select a vehicle first');
      return;
    }

    const documentMapping = {
      'rc': 'registrationCertificate',
      'insurance': 'insuranceCertificate',
      'puc': 'pucCertificate',
      'permits': 'requiredPermits'
    };

    const documentsToUpload = Object.keys(this.documents)
      .reduce((acc, key) => {
        if (this.documents[key].pdfFile) {
          acc[documentMapping[key]] = this.documents[key].pdfFile;
        }
        return acc;
      }, {} as {[key: string]: File});

    if (Object.keys(documentsToUpload).length === 0) {
      this.showError('No documents to upload');
      return;
    }

    this.isUploading = true;
    this.cdr.detectChanges();

    this.vehicleService.uploadVehicleDocuments(this.vehicleId, documentsToUpload)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isUploading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (vehicle: Vehicle) => {
          this.showSuccess('All documents uploaded successfully');

          Object.keys(documentsToUpload).forEach(key => {
            const mappedType = Object.keys(documentMapping).find(
              k => documentMapping[k] === key
            );

            if (mappedType) {
              this.loadAndSetDocument(mappedType, this.getDocumentFromVehicle(vehicle, mappedType));
            }
          });
        },
        error: () => {
          this.showError('Error uploading documents');
        }
      });
  }

  removeFile(documentType: string): void {
    this.documents[documentType].pdfFile = null;
    this.documents[documentType].pdfPreview = null;
    this.documents[documentType].error = null;
    this.cdr.detectChanges();
  }

  private getDocumentFromVehicle(vehicle: Vehicle, documentType: string): string {
    const documentMap = {
      rc: vehicle.registrationCertificate,
      insurance: vehicle.insuranceCertificate,
      puc: vehicle.pucCertificate,
      permits: vehicle.requiredPermits
    };
    return documentMap[documentType];
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['success-snackbar'],
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['error-snackbar'],
    });
  }

  hasAnyDocuments(): boolean {
    return Object.values(this.documents).some(doc => doc.pdfFile !== null);
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  getFileSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} bytes`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }
  toggleDocumentExpansion(documentType: DocumentType): void {
    documentType.isExpanded = !documentType.isExpanded;
  }
}
