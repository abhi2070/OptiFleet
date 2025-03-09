import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Inject, Input, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { AppState, DeviceService } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { VehicleInfo } from '@app/shared/models/vehicle.model';
import { EntityType, PageLink } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { map, Observable, startWith } from 'rxjs';

@Component({
  selector: 'tb-vehicle-detail',
  templateUrl: './vehicle-detail.component.html',
  styleUrls: ['./vehicle-detail.component.scss']
})
export class VehicleDetailComponent extends EntityComponent<VehicleInfo> implements OnInit {
  @Input() editModeToggled: EventEmitter<void>;
  @ViewChild('deviceInput') deviceInput: ElementRef<HTMLInputElement>;

  entityForm: FormGroup;
  entityType = EntityType;
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

  devices = [];
  vehicleScope: 'tenant';
    pageLink: PageLink = new PageLink(100);

  private convertMillisToDate(millis: number): Date | null {
    return millis ? new Date(millis) : null;
  }

  private convertDateToMillis(date: Date): number | null {
    return date ? date.getTime() : null;
  }

  constructor(
    protected store: Store<AppState>,
    private dialog: MatDialog,
    protected translate: TranslateService,
    protected router: Router,
    public fb: UntypedFormBuilder,
    private route: ActivatedRoute,
    private deviceService:DeviceService,
    @Inject('entity') protected entityValue: VehicleInfo,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<VehicleInfo>,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  ngOnInit(): void {
    this.vehicleScope = this.entitiesTableConfig.componentsData.vehicleScope;
    this.fetchAvailableDevices();
    super.ngOnInit();
  }

  buildForm(entity: VehicleInfo): FormGroup {
    return this.fb.group({
      vehiclenumber: [entity ? entity.vehiclenumber : '', [Validators.required]],
      vehicleType: [entity ? entity.type : '', [Validators.required]],
      nextService: [entity ? this.convertMillisToDate(entity.nextService) : '', [Validators.required]],
      status: [entity ? entity.status : 'AVAILABLE', [Validators.required]],
      device: [entity ? entity.device : '',]
    });
  }

  updateForm(entity: VehicleInfo) {
    this.selectedDevices = [];
    if (entity.device && entity.device.length > 0 && this.devices.length > 0) {
      this.selectedDevices = this.devices.filter(device =>
        entity.device.includes(device.id.id)
      );
    }

    this.entityForm.patchValue({
      vehiclenumber: entity.vehiclenumber,
      vehicleType: entity.type,
      nextService: this.convertMillisToDate(entity.nextService),
      status: entity.status,
      device: this.selectedDevices
    });
    this.deviceCtrl.setValue('');
  }

  private setupDeviceFilter(): void {
    this.filteredDevices = this.deviceCtrl.valueChanges.pipe(
      startWith(null),
      map((searchText: string | null | any) => {
        if (typeof searchText === 'string') {
          return this.filterDevices(searchText);
        }
        return this.devices.filter(device =>
          !this.selectedDevices.some(selected => selected.id.id === device.id.id)
        );
      })
    );
  }

  private filterDevices(value: string): any[] {
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


  onVehicleIdCopied($event: any) {
    this.store.dispatch(new ActionNotificationShow({
      message: this.translate.instant('vehicle.idCopiedMessage'),
      type: 'success',
      duration: 750,
      verticalPosition: 'bottom',
      horizontalPosition: 'right'
    }));
  }

  hideDelete() {
    return this.entitiesTableConfig ? !this.entitiesTableConfig.deleteEnabled(this.entity) : false;
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

  prepareFormValue(): VehicleInfo {
    const formValue = this.entityForm.value;
    return {
      ...formValue,
      type: formValue.vehicleType,
      nextService: this.convertDateToMillis(formValue.nextService),
      deviceId: formValue.device
    } as VehicleInfo;
  }

  displayFn(device: any): string {
    return device ? device.name : '';
  }
}
