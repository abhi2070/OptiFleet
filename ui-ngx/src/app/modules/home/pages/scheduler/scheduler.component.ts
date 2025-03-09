/* eslint-disable max-len */
/* eslint-disable prefer-arrow/prefer-arrow-functions */
import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { EntityType } from '@app/shared/models/entity-type.models';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { EntityComponent } from '../../components/entity/entity.component';
import { DatePipe } from '@angular/common';
import { SchedulersInfo } from '@app/shared/models/schedulers.model';
import { Authority, Direction, PageLink } from '@app/shared/public-api';
import { MatDialogRef } from '@angular/material/dialog';
import { AddEntityDialogComponent } from '../../components/entity/add-entity-dialog.component';
import { DeviceService, getCurrentAuthUser } from '@app/core/public-api';

interface Repeat {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'tb-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.scss']
})
export class SchedulerComponent extends EntityComponent<SchedulersInfo> implements OnInit {

  @ViewChild('reportFilterPanel') reportFilterPanel: TemplateRef<any>;
  @Output() filterChange = new EventEmitter<void>();

  @Input()
  isStateForm: boolean;

  @Input()
  deviceName: string;

  reportCategory = [
    { id: '1', name: 'Device Name' },
    { id: '2', name: 'Device Type' },
  ];
  entityForm: FormGroup;
  selectedValue: string;
  entityType = EntityType;
  timeZoneControl = new FormControl('');
  schedulerScope: 'tenant';
  currentDateAndTime: string;
  startTime: string;
  showStartTime = false;
  campaignOne: FormGroup<{ startdatetime: FormControl<Date> }>;
  reportTypes = ['CSV', 'Excel'];
  selectedCategory: string;
  availableOptions: { value: string; label: string }[] = [];
  availableDevice: any[] = [];
  pageLink:any;


  currentOperation = '';
  operations: string[] = [];
  allOperation: string[] = [
    'All', 'Create', 'Delete', 'RPC Call', 'Read', 'Read Attribute',
    'Read Credentials', 'Read Telemetry', 'Share Group', 'Write',
    'Write attribute', 'Write Credentials', 'Write Telemetry',
  ];

  authUser: any;
  schedulerComponent: any;
  submitted = true;
  isScheduleEnabled = false;
  isRepeatEnabled = false;

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    private datePipe: DatePipe,
    private deviceService: DeviceService,
    @Inject('entity') protected entityValue: SchedulersInfo,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<SchedulersInfo>,
    public fb: FormBuilder,
    public dialogRef: MatDialogRef<AddEntityDialogComponent>,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
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
  }

  toggleSchedule(isChecked: boolean) {
    this.isScheduleEnabled = isChecked;
    if (!isChecked) {
      this.entityForm.get('timeZone').disable();
      this.campaignOne.disable();
    } else {
      this.entityForm.get('timeZone').enable();
      this.campaignOne.enable();
    }
  }

  toggleRepeat(isChecked: boolean) {
    this.isRepeatEnabled = isChecked;
  }

  ngOnInit() {
    this.schedulerScope = this.entitiesTableConfig.componentsData.schedulerScope;
    super.ngOnInit();
    this.currentDateAndTime = this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss');
    this.fetchAllDevice();
  }

  entityFormValue() {
    const formValue = this.entityForm ? { ...this.entityForm.getRawValue() } : {};
    if (formValue.endDate) {
      formValue.endDate = new Date(formValue.endDate).toISOString();
    }
    if (formValue.start) {
      formValue.start = new Date(formValue.start).toISOString();
    }
    return this.prepareFormValue(formValue);
  }

  hideDelete() {
    return this.entitiesTableConfig ? !this.entitiesTableConfig.deleteEnabled(this.entity) : false;
  }

  remove(operation: string): void {
    const index = this.operations.indexOf(operation);
    if (index >= 0) {
      this.operations.splice(index, 1);
    }
  }

  selected(event: any): void {
    const value = event.option.value;
    if (value && !this.operations.includes(value)) {
      this.operations.push(value);
    }
    this.currentOperation = '';
  }

  forms: { topic: string; qos: string }[] = [];
  addForm() {
    this.forms.push({ topic: '', qos: '' });
  }

  updateForm(entity: SchedulersInfo) {
    if (entity) {
      this.entityForm.patchValue({
        name: entity.name,
        endDate: entity.endDate ? new Date(entity.endDate).toISOString() : null,
        start: entity.start ? new Date(entity.start).toISOString() : null,
        reportNamePattern: entity.reportNamePattern,
        reportType: entity.reportType,
        toAddress: entity.toAddress,
        cc: entity.cc,
        bcc: entity.bcc,
        repeatSchedule: entity.repeatSchedule,
        subject: entity.subject,
        schedule: entity.schedule,
        repeat: entity.repeat,
        timeZone: entity.timeZone,
        body: entity.body,
        reportCategory:entity.reportCategory,
        reportAvailable:entity.reportAvailable
      });
    }
  }

  buildForm(entity: SchedulersInfo): FormGroup {
    const currentDate = new Date().toISOString();
    return this.fb.group({
      name: [entity ? entity.name : '', [Validators.required, Validators.maxLength(255)]],
      endDate: [entity ? (entity.endDate ? new Date(entity.endDate).toISOString() : null) : null],
      start: [entity?.start ? new Date(entity.start).toISOString() : currentDate],
      reportNamePattern: [entity?.reportNamePattern ? entity.reportNamePattern : 'report-%d{yyyy-MM-dd_HH:mm:ss}', [Validators.required]],
      reportType: [entity?.reportType ? entity.reportType : 'CSV', [Validators.required]],
      toAddress: [entity ? entity.toAddress : '', [Validators.required, Validators.email]],
      cc: [entity ? entity.cc : '', []],
      bcc: [entity ? entity.bcc : '', []],
      repeatSchedule: [entity?.repeatSchedule ? entity.repeatSchedule : 'Daily-0', []],
      subject: [entity?.subject ? entity.subject : 'Report generated on %d{yyyy-MM-dd HH:mm:ss}', [Validators.required]],
      schedule: [entity ? entity.schedule : ''],
      repeat: [entity ? entity.repeat : ''],
      timeZone: [entity?.timeZone ? entity.timeZone : 'Asia/Calcutta'],
      body: [entity?.body ? entity.body : 'Report was successfully generated on %d{yyyy-MM-dd HH:mm:ss}.See attached report file.', []],
      reportCategory:[entity? entity.reportCategory: ''],
      reportAvailable:[entity? entity.reportAvailable:'']
    });
  }


  onSchedulerProfileUpdated() {
    this.entitiesTableConfig.updateData(false);
  }

  onDateTimeChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    const dateTimeString = inputElement.value;
    const dateTime = new Date(dateTimeString);
    this.campaignOne.patchValue({ startdatetime: dateTime });
  }

  formatDateTime(dateTime: Date): string {
    return dateTime.toISOString().slice(0, 16);
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
          this.onCategoryChange({ value: this.entityForm.get('reportCategory').value });
        },
        error: (error) => console.error('Error:', error)
      });
    }
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

  timeZones: { value: string; viewValue: string }[] = [
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
}
