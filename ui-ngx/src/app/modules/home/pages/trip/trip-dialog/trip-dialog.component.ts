import { Component, OnInit, NgZone, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { DriverService } from '@app/core/http/driver.service';
import { TripService } from '@app/core/http/trip.service';
import { VehicleService } from '@app/core/http/vehicle.service';
import { Driver } from '@app/shared/models/driver.model';
import { PageLink } from '@app/shared/models/page/page-link';
import { Direction } from '@app/shared/models/page/sort-order';
import { Position } from '@app/shared/models/trip.model';
import { Vehicle } from '@app/shared/models/vehicle.model';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { startWith, map, finalize, takeUntil } from 'rxjs/operators';

declare var google: any;

@Component({
  selector: 'app-trip-dialog',
  templateUrl: './trip-dialog.component.html',
  styleUrls: ['./trip-dialog.component.scss']
})
export class TripDialogComponent implements OnInit, OnDestroy {
  tripForm: FormGroup;
  private loadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.loadingSubject.asObservable();
  private destroy$ = new Subject<void>();
  
  pageLink: PageLink = new PageLink(100, 0, '', { property: 'name', direction: Direction.ASC });
  pageLink1: PageLink = new PageLink(100, 0, '', { property: 'vehiclenumber', direction: Direction.ASC });

  vehicleTypeOptions = [
    { value: 'FOUR_WHEELER', label: 'Four Wheeler' },
    { value: 'SIX_WHEELER', label: 'Six Wheeler' },
    { value: 'TEN_WHEELER', label: 'Ten Wheeler' }
  ];

  tripStatusOptions = [
    { value: 'SCHEDULED', label: 'Scheduled' },
    { value: 'IN_PROGRESS', label: 'In Progress' },
    { value: 'COMPLETED', label: 'Completed' },
    { value: 'CANCELLED', label: 'Cancelled' }
  ];

  availableVehicles$: Observable<Vehicle[]>;
  availableDrivers$: Observable<Driver[]>;
  private googleAutocompleteListeners: { [key: string]: google.maps.MapsEventListener } = {};

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<TripDialogComponent>,
    private tripService: TripService,
    private driverService: DriverService,
    private vehicleService: VehicleService,
    private ngZone: NgZone,
  ) {}

  ngOnInit() {
    this.buildForm();
    this.setupVehicleTypeListener();
    this.loadAvailableDrivers();
    this.initGooglePlacesAutocomplete();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.cleanupGoogleAutoComplete();
  }

  private cleanupGoogleAutoComplete() {
    Object.values(this.googleAutocompleteListeners).forEach(listener => {
      google?.maps?.event?.removeListener(listener);
    });
  }

  private setupVehicleTypeListener() {
    this.availableVehicles$ = combineLatest([
      this.vehicleService.getVehicleInfos(this.pageLink1),
      this.tripForm.get('vehicleType').valueChanges.pipe(
        startWith(this.tripForm.get('vehicleType').value)
      )
    ]).pipe(
      takeUntil(this.destroy$),
      map(([pageData, selectedType]) => {
        return pageData.data
          .filter(vehicle => vehicle.status === 'AVAILABLE')
          .filter(vehicle => !selectedType || vehicle.type === selectedType);
      })
    );
  }

  private loadAvailableDrivers() {
    this.availableDrivers$ = this.driverService.getDriverInfos(this.pageLink).pipe(
      takeUntil(this.destroy$),
      map(pageData => pageData.data.filter(driver => driver.status === 'AVAILABLE'))
    );
  }

  private buildForm(): void {
    this.tripForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      vehicleType: ['', Validators.required],
      assignedVehicle: ['', Validators.required],
      assignedDriver: [[], [Validators.required, Validators.minLength(1)]],
      status: ['SCHEDULED', Validators.required],
      sourcePosition: this.fb.group({
        address: ['', Validators.required],
        latitude: ['', [Validators.required, Validators.min(-90), Validators.max(90)]],
        longitude: ['', [Validators.required, Validators.min(-180), Validators.max(180)]],
      }),
      destinationPosition: this.fb.group({
        address: ['', Validators.required],
        latitude: ['', [Validators.required, Validators.min(-90), Validators.max(90)]],
        longitude: ['', [Validators.required, Validators.min(-180), Validators.max(180)]],
      })
    }, {
      validators: [this.dateRangeValidator]
    });
  }

  private initGooglePlacesAutocomplete() {
    if (!google?.maps?.places) {
      console.error('Google Maps Places API not loaded');
      return;
    }

    try {
      this.setupAutocomplete('sourceAddress', 'sourcePosition');
      this.setupAutocomplete('destinationAddress', 'destinationPosition');
    } catch (error) {
      console.error('Error initializing Google Places Autocomplete:', error);
    }
  }

  private setupAutocomplete(elementId: string, formGroupName: string) {
    const input = document.getElementById(elementId);
    if (!input) {
      console.error(`Element with id ${elementId} not found`);
      return;
    }

    const autocomplete = new google.maps.places.Autocomplete(input);
    this.googleAutocompleteListeners[elementId] = autocomplete.addListener('place_changed', () => {
      const place = autocomplete.getPlace();
      this.ngZone.run(() => {
        this.updatePosition(formGroupName, place);
      });
    });
  }

  private updatePosition(formGroupName: string, place: google.maps.places.PlaceResult) {
    if (place.geometry?.location) {
      const position: Position = {
        address: place.formatted_address || '',
        latitude: place.geometry.location.lat(),
        longitude: place.geometry.location.lng()
      };
      
      this.tripForm.get(formGroupName).patchValue(position);
    }
  }

  onVehicleTypeChange(event: any) {
    this.tripForm.get('assignedVehicle').setValue('');
  }

  private dateRangeValidator(group: FormGroup) {
    const start = group.get('startDate').value;
    const end = group.get('endDate').value;
    
    if (start && end) {
      const startTime = start instanceof Date ? start.getTime() : new Date(start).getTime();
      const endTime = end instanceof Date ? end.getTime() : new Date(end).getTime();
      
      if (startTime > endTime) {
        return { dateRange: true };
      }
    }
    return null;
  }

  save() {
    if (!this.validateForm()) {
      return;
    }

    const formValue = this.tripForm.value;
    const tripData = {
      ...formValue,
      startDate: this.getTimestamp(formValue.startDate),
      endDate: this.getTimestamp(formValue.endDate)
    };

    this.loadingSubject.next(true);
    this.tripForm.disable();

    this.tripService.saveTrip(tripData)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.loadingSubject.next(false);
          this.tripForm.enable();
        })
      )
      .subscribe({
        next: (savedTrip) => {
          this.dialogRef.close(savedTrip);
        },
        error: (error) => {
          console.error('Error saving trip:', error);
        }
      });
  }

  private getTimestamp(date: Date | string | null): number | null {
    if (!date) return null;
    return new Date(date).getTime();
  }

  private validateForm(): boolean {
    if (this.tripForm.invalid) {
      Object.keys(this.tripForm.controls).forEach(key => {
        const control = this.tripForm.get(key);
        if (control instanceof FormGroup) {
          Object.keys(control.controls).forEach(childKey => {
            control.get(childKey).markAsTouched();
          });
        } else {
          control.markAsTouched();
        }
      });
      return false;
    }
    return true;
  }

  cancel() {
    this.dialogRef.close();
  }
}