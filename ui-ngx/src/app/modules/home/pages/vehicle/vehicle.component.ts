import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { VehicleService } from '@app/core/http/vehicle.service';
import { VehicleInfo } from '@app/shared/models/vehicle.model';
import { DeviceService } from '@app/core/public-api';
import { PageLink } from '@shared/models/page/page-link';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'tb-vehicle',
  templateUrl: './vehicle.component.html',
  styleUrls: ['./vehicle.component.scss']
})
export class VehicleComponent extends DialogComponent<VehicleComponent> implements OnInit {
  @ViewChild('deviceInput') deviceInput: ElementRef<HTMLInputElement>;

  entityForm: FormGroup;
  deviceCtrl = new FormControl('');
  selectedDevices: any[] = [];
  filteredDevices: Observable<any[]>;

  vehicleTypes = [
    { value: 'FOUR_WHEELER', label: 'Four Wheeler' },
    { value: 'SIX_WHEELER', label: 'Six Wheeler' },
    { value: 'TEN_WHEELER', label: 'Ten Wheeler' }
  ];

  vehicleStatuses = [
    { value: 'AVAILABLE', label: 'Available' },
    { value: 'ON_TRIP', label: 'On Trip' },
    { value: 'MAINTENANCE', label: 'Maintenance' }
  ];

  devices: any[] = [];
  pageLink: PageLink = new PageLink(100);

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    private fb: FormBuilder,
    private vehicleService: VehicleService,
    private deviceService: DeviceService,
    public dialogRef: MatDialogRef<VehicleComponent>
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit(): void {
    this.buildForm();
    this.fetchAvailableDevices();
  }

  private buildForm(): void {
    this.entityForm = this.fb.group({
      vehiclenumber: ['', [Validators.required]],
      vehicleType: ['', [Validators.required]],
      nextService: [new Date(), [Validators.required]],
      device: [[]]
    });
  }



private setupDeviceFilter(): void {
  this.filteredDevices = this.deviceCtrl.valueChanges.pipe(
    startWith(null),
    map((searchText: string | null | any) => {
      if (typeof searchText === 'string') {
        return this._filterDevices(searchText);
      }
      return this.devices.filter(device =>
        !this.selectedDevices.find(selected => selected.id.id === device.id.id)
      );
    })
  );
}

private _filterDevices(value: string): any[] {
  const filterValue = value?.toLowerCase() || '';
  return this.devices.filter(device =>
    device.name.toLowerCase().includes(filterValue) &&
    !this.selectedDevices.find(selected => selected.id.id === device.id.id)
  );
}

  selected(event: MatAutocompleteSelectedEvent): void {
    const device = event.option.value;
    this.selectedDevices.push(device);
    this.deviceInput.nativeElement.value = '';
    this.deviceCtrl.setValue('');
    this.updateDevicesFormControl();
  }

  removeDevice(device: any): void {
    const index = this.selectedDevices.indexOf(device);
    if (index >= 0) {
      this.selectedDevices.splice(index, 1);
      this.updateDevicesFormControl();
    }
  }

  private updateDevicesFormControl(): void {
    this.entityForm.get('device').setValue(
      this.selectedDevices.map(device => device.id.id)
    );
  }

  save(): void {
    if (this.entityForm.valid) {
      const vehicleData = this.prepareVehicleData();

      this.vehicleService.saveVehicle(vehicleData).subscribe({
        next: (response) => {
          this.dialogRef.close(response);
        },
        error: (error) => {
          console.error('Error saving vehicle:', error);
        }
      });
    }
  }

  private prepareVehicleData(): VehicleInfo {
    const formValue = this.entityForm.getRawValue();
    const nextServiceDate = formValue.nextService;

    let nextServiceTimestamp: number | null = null;

    if (nextServiceDate) {
      if (typeof nextServiceDate === 'string') {
        nextServiceTimestamp = new Date(nextServiceDate).getTime();
      } else if (nextServiceDate instanceof Date) {
        nextServiceTimestamp = nextServiceDate.getTime();
      }
    }

    return {
      ...formValue,
      type: formValue.vehicleType,
      nextService: nextServiceTimestamp
    };
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

  updateForm(entity: VehicleInfo) {
    if (entity) {
      this.entityForm.patchValue({
        vehiclenumber: entity.vehiclenumber,
        vehicleType: entity.type,
        nextService: entity.nextService ? new Date(entity.nextService) : new Date(),
        device: entity.device
      });
      if (entity.device && Array.isArray(entity.device)) {
        this.selectedDevices = this.devices.filter(device =>
          entity.device.includes(device.id.id)
        );
      }
    }
  }

  private fetchAvailableDevices(): void {
    this.deviceService.getTenantDeviceInfos(this.pageLink).subscribe({
      next: (data) => {
        this.devices = data.data;
        this.setupDeviceFilter();
      },
      error: (error) => {
        console.error('Error fetching devices:', error);
      },
    });
  }

  getDisplayLabel(value: string, options: Array<{ value: string, label: string }>): string {
    const option = options.find(opt => opt.value === value);
    return option ? option.label : value;
  }

  displayFn(device: any): string {
    return device ? device.name : '';
  }
}
