import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { DriverService } from '@app/core/http/driver.service';
import { DriverInfo } from '@app/shared/models/driver.model';

@Component({
  selector: 'tb-driver',
  templateUrl: './driver.component.html',
  styleUrls: ['./driver.component.scss']
})
export class DriverComponent extends DialogComponent<DriverComponent> implements OnInit {

  entityForm: FormGroup;

  genderOptions = ['Male', 'Female', 'Other'];

  driverStatuses = [
    { value: 'AVAILABLE', label: 'Available' },
    { value: 'ON_TRIP', label: 'On Trip' },
    { value: 'ON_LEAVE', label: 'Leave' },
    { value: 'DISCONTINUED', label: 'Discontinued' }
  ];

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    private fb: FormBuilder,
    private driverService: DriverService,
    public dialogRef: MatDialogRef<DriverComponent>
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.entityForm = this.fb.group({
      name: ['', [Validators.required]],
      gender: ['', [Validators.required]],
      dateOfBirth: [null, [Validators.required]],
      serviceStartDate: [null, [Validators.required]],
      drivingLicenseNumber: ['', [Validators.required]],
      drivingLicenseValidity: [null, [Validators.required]],
    });
  }

  save(): void {
    if (this.entityForm.valid) {
      const driverData = this.prepareDriverData();
      this.driverService.saveDriver(driverData).subscribe({
        next: (response) => {
          this.dialogRef.close(response);
        },
        error: (error) => {
          console.error('Error saving driver:', error);
        }
      });
    } else {
      this.markFormGroupTouched(this.entityForm);
    }
  }

  private prepareDriverData(): DriverInfo {
    const formValue = this.entityForm.getRawValue();
    return {
      ...formValue,
      dateOfBirth: this.getTimestamp(formValue.dateOfBirth),
      serviceStartDate: this.getTimestamp(formValue.serviceStartDate),
      drivingLicenseValidity: this.getTimestamp(formValue.drivingLicenseValidity)
    };
  }

  private getTimestamp(date: Date | null): number | null {
    return date ? new Date(date).getTime() : null;
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }

  updateForm(entity: DriverInfo) {
    if (entity) {
      this.entityForm.patchValue({
        name: entity.name,
        gender: entity.gender,
        dateOfBirth: entity.dateOfBirth ? new Date(entity.dateOfBirth) : null,
        serviceStartDate: entity.serviceStartDate ? new Date(entity.serviceStartDate) : null,
        drivingLicenseNumber: entity.drivingLicenseNumber,
        drivingLicenseValidity: entity.drivingLicenseValidity ? new Date(entity.drivingLicenseValidity) : null,
      });
    }
  }
}
