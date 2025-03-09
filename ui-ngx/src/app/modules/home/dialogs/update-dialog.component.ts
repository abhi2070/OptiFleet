import { ChangeDetectorRef, Component, Inject, OnInit, OnDestroy, SkipSelf, EventEmitter, Output, Input } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  FormGroupDirective,
  NgForm,
  Validators,
  FormGroup,
  FormControl
} from '@angular/forms';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { Schedulers } from '@app/shared/models/schedulers.model';
import { DatePipe } from '@angular/common';
import { SchedulersService } from '@app/core/http/schedulers.service';
import { Observable, ReplaySubject, Subscription } from 'rxjs';
import { EntityType } from '@shared/models/entity-type.models';
import { BaseData, HasId } from '@app/shared/models/base-data';
import { mergeDeep } from '@app/core/utils';
import { EntityTableConfig } from '../models/entity/entities-table-config.models';
import { Authority, Direction, PageLink } from '@app/shared/public-api';
import { DeviceService, getCurrentAuthUser } from '@app/core/public-api';

export interface UpdateDialogData {
  entityIds: string[];
  entityType: EntityType;
  entity: BaseData<HasId>;
  entitiesTableConfig: EntityTableConfig<BaseData<HasId>>;
  prefillData?: Schedulers;
}

interface Repeat {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'tb-update-dialog',
  templateUrl: './update-dialog.component.html',
  providers: [{ provide: ErrorStateMatcher, useExisting: UpdateDialogComponent }],
  styleUrls: ['./update-dialog.component.scss']
})
export class UpdateDialogComponent extends DialogComponent<UpdateDialogComponent, boolean> implements ErrorStateMatcher, OnInit, OnDestroy {

  @Output() entityUpdated = new EventEmitter<BaseData<HasId>>();

  editSchedulerFormGroup: FormGroup;
  entityType = EntityType;
  currentDateAndTime: string;
  isScheduleEnabled = false;
  isRepeatEnabled = false;
  reportTypes = ['CSV', 'Excel'];
  campaignOne: FormGroup<{ startdatetime: FormControl<Date> }>;
  selectedCategory: string;
  availableOptions: { value: string; label: string }[] = [];
  availableDevice: any[] = [];
  pageLink:any;
  reportCategory = [
    { id: '1', name: 'Device Name' },
    { id: '2', name: 'Device Type' },
  ];
  timeZones = [
    { value: 'Pacific/Honolulu', viewValue: 'Pacific/Honolulu' },
    { value: 'Pacific/Los_Angeles', viewValue: 'Pacific/Los Angeles' },
    { value: 'Pacific/Juneau', viewValue: 'Pacific/Juneau' },
    { value: 'America/Boise', viewValue: 'America/Boise' },
    { value: 'America/Phoenix', viewValue: 'America/Phoenix' },
    { value: 'America/Chicago', viewValue: 'America/Chicago' },
    { value: 'America/New_York', viewValue: 'America/New York' },
    { value: 'America/Indiana/Knox', viewValue: 'America/Indiana/Knox' },
    { value: 'America/Halifax', viewValue: 'America/Halifax' },
    { value: 'America/Argentina/Buenos_Aires', viewValue: 'America/Argentina/Buenos Aires' },
    { value: 'Europe/Lisbon', viewValue: 'Europe/Lisbon' },
    { value: 'Europe/London', viewValue: 'Europe/London' },
    { value: 'Europe/Amsterdam', viewValue: 'Europe/Amsterdam' },
    { value: 'Europe/Belgrade', viewValue: 'Europe/Belgrade' },
    { value: 'Europe/Istanbul', viewValue: 'Europe/Istanbul' },
    { value: 'Europe/Moscow', viewValue: 'Europe/Moscow' },
    { value: 'Europe/Athens', viewValue: 'Europe/Athens' },
    { value: 'Asia/Shanghai', viewValue: 'Asia/Shanghai' },
    { value: 'Asia/Tokyo', viewValue: 'Asia/Tokyo' },
    { value: 'Asia/Calcutta', viewValue: 'Asia/Calcutta (UTC+05:30)' }
  ];
  repeats: Repeat[] = [
    { value: 'Daily-0', viewValue: 'Daily' },
    { value: 'Monthly-1', viewValue: 'Monthly' },
    { value: 'Yearly-2', viewValue: 'Yearly' },
  ];
  @Input()
  isStateForm: boolean;

  @Input()
  deviceName: string;


  isFormDirty = false;
  private formChangeSubscription: Subscription;
  authUser: any;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    private datePipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public data: UpdateDialogData,
    private schedulerService: SchedulersService,
    private deviceService: DeviceService,
    private fb: UntypedFormBuilder,
    protected cd: ChangeDetectorRef,
    @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
    public dialogRef: MatDialogRef<UpdateDialogComponent, boolean>
  ) {
    super(store, router, dialogRef);
    this.authUser=getCurrentAuthUser(this.store)
    const today = new Date();
    const month = today.getMonth();
    const year = today.getFullYear();
    const day = today.getDate();
    const hours = today.getHours();
    const minutes = today.getMinutes();
    this.campaignOne = new FormGroup({
      startdatetime: new FormControl(new Date(year, month, day, hours, minutes))
    });

    this.editSchedulerFormGroup = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(255)]],
      endDate: [{ value: null, disabled: true }],
      start: [{ value: null, disabled: true }, Validators.required],
      reportNamePattern: ['report-%d{yyyy-MM-dd_HH:mm:ss}', [Validators.required]],
      reportType: ['CSV', Validators.required],
      toAddress: ['', [Validators.required, Validators.email]],
      cc: [''],
      bcc: [''],
      subject: ['Report generated on %d{yyyy-MM-dd HH:mm:ss}', Validators.required],
      schedule: [false],
      repeat: [false],
      body: ['Report was successfully generated on %d{yyyy-MM-dd HH:mm:ss}.\nSee attached report file.'],
      timeZone: [{ value: '', disabled: true }, Validators.required],
      repeatSchedule: [{ value: '', disabled: true }],
      reportCategory:[''],
      reportAvailable:['']
    });
  }

  ngOnInit(): void {
    if (this.data.prefillData) {
      this.updateForm(this.data.prefillData);
    }
    this.fetchAllDevice();

    this.currentDateAndTime = this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss');

    this.editSchedulerFormGroup.get('schedule').valueChanges.subscribe(isScheduled => {
      this.toggleSchedule(isScheduled);
    });

    this.editSchedulerFormGroup.get('repeat').valueChanges.subscribe(isRepeated => {
      this.toggleRepeat(isRepeated);
    });

    this.formChangeSubscription = this.editSchedulerFormGroup.valueChanges.subscribe(() => {
      this.isFormDirty = true;
    });
  }

  ngOnDestroy(): void {
    if (this.formChangeSubscription) {
      this.formChangeSubscription.unsubscribe();
    }
  }

  updateForm(entity: Schedulers): void {
    this.editSchedulerFormGroup.patchValue({
      name: entity.name,
      endDate: entity.endDate ? new Date(entity.endDate) : null,
      start: entity.start ? new Date(entity.start) : null,
      reportNamePattern: entity.reportNamePattern,
      reportType: entity.reportType,
      toAddress: entity.toAddress,
      cc: entity.cc,
      bcc: entity.bcc,
      subject: entity.subject,
      schedule: entity.schedule,
      repeat: entity.repeat,
      body: entity.body,
      timeZone: entity.timeZone,
      repeatSchedule: entity.repeatSchedule,
      reportCategory:entity.reportCategory,
      reportAvailable:entity.reportAvailable

    });

    this.toggleSchedule(entity.schedule);
    this.toggleRepeat(entity.repeat);
  }

  toggleSchedule(isChecked: boolean): void {
    this.isScheduleEnabled = isChecked;
    const timeZoneControl = this.editSchedulerFormGroup.get('timeZone');
    const startControl = this.editSchedulerFormGroup.get('start');
    if (isChecked) {
      timeZoneControl?.enable();
      startControl?.enable();
    } else {
      timeZoneControl?.disable();
      startControl?.disable();
      this.editSchedulerFormGroup.get('repeat').setValue(false);
    }
  }

  toggleRepeat(isChecked: boolean): void {
    this.isRepeatEnabled = isChecked;
    const endDateControl = this.editSchedulerFormGroup.get('endDate');
    const repeatScheduleControl = this.editSchedulerFormGroup.get('repeatSchedule');
    if (isChecked) {
      endDateControl?.enable();
      repeatScheduleControl?.enable();
    } else {
      endDateControl?.disable();
      repeatScheduleControl?.disable();
    }
  }

  onDateTimeChange(event: any): void {
    const dateTime = event.value;
    this.editSchedulerFormGroup.patchValue({ start: dateTime });
  }

  isErrorState(control: UntypedFormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }

  cancel(): void {
    this.dialogRef.close(false);
  }

  update(): void {
    this.saveEntity(false).subscribe(
      (entity) => {
        if (entity) {
          this.onUpdateEntity();
        }
      },
      (error) => {
        console.error('Error updating entity:', error);
        this.showErrorMessage('Failed to update entity. Please try again.');
      }
    );
  }

  private saveEntity(emitEntityUpdated = true): Observable<BaseData<HasId>> {
    const saveEntitySubject = new ReplaySubject<BaseData<HasId>>(1);
    if (this.editSchedulerFormGroup.valid) {
      const editingEntity = { ...this.data.entity, ...this.editSchedulerFormGroup.value };

      this.data.entitiesTableConfig.saveEntity(editingEntity, this.data.entity).subscribe(
        (entity) => {
          this.data.entity = entity;
          if (emitEntityUpdated) {
            this.entityUpdated.emit(entity);
          }
          saveEntitySubject.next(entity);
          saveEntitySubject.complete();
        },
        (error) => {
          console.error('Error saving entity:', error);
          this.showErrorMessage('Failed to save entity. Please try again.');
          saveEntitySubject.error(error);
        }
      );
    } else {
      this.handleFormErrors();
      saveEntitySubject.next(null);
      saveEntitySubject.complete();
    }
    return saveEntitySubject.asObservable();
  }

  private onUpdateEntity(): void {
    this.dialogRef.close(true);
  }


  private handleFormErrors(): void {
    const formControls = this.editSchedulerFormGroup.controls;

    Object.keys(formControls).forEach(controlName => {
      const control = formControls[controlName];
      if (control.invalid) {
        console.log(`Field '${controlName}' is invalid:`, control.errors);
      }
    });

    this.editSchedulerFormGroup.markAllAsTouched();
    this.showErrorMessage('Please correct the errors in the form and try again.');
  }

  private showErrorMessage(message: string): void {
    console.error(message);
    // TODO: Implement UI notification here
  }

  formatDateTime(dateTime: Date ): string {

    return dateTime.toISOString().slice(0, 16);
  }


  onCategoryChange(event: any): void {
    this.selectedCategory = event.value;
    this.availableOptions = this.getAvailableOptionsForCategory(this.selectedCategory);
  }
  getAvailableOptionsForCategory(categoryId: string): { value: string; label: string }[] {
    if (categoryId === '1') {
      return this.availableDevice.map(device => (
        { value: device.id.id, label: device.name }
      ));
    }
    else if (categoryId === '2') {
      return [
        { value: 'indoor', label: 'Indoor' },
        { value: 'outdoor', label: 'Outdoor' },
      ];
    }
    return [];
  }
  fetchAllDevice(): void {
    const entityNameFilter = this.isStateForm && this.deviceName ? this.deviceName : '';
    this.pageLink = new PageLink(10, 0, entityNameFilter, {
      property: 'name',
      direction: Direction.DESC
    });

    if (this.authUser.authority === Authority.TENANT_ADMIN) {
      this.deviceService.getTenantDeviceInfos(this.pageLink, '', { ignoreLoading: true }).subscribe({
        next: (data) => {
          this.availableDevice = data.data;
          this.onCategoryChange({ value: this.editSchedulerFormGroup.get('reportCategory').value });
        },
        error: (error) => console.error('Error:', error)
      });
    }
  }
}
