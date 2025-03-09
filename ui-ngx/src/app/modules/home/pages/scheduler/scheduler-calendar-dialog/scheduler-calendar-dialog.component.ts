import { DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, Inject, ViewChild } from '@angular/core';
import { FormControl, FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppState } from '@app/core/core.state';
import { EntityType } from '@app/shared/models/entity-type.models';
import { Store } from '@ngrx/store';

interface repeat {
  value: string;
  viewValue: string;
}

interface DialogData {
  date: Date; 
}

@Component({
  selector: 'tb-scheduler-calendar-dialog',
  templateUrl: './scheduler-calendar-dialog.component.html',
  styleUrls: ['./scheduler-calendar-dialog.component.scss']
})
export class SchedulerCalendarDialogComponent {

  @ViewChild('scheduler') scheduler: ElementRef;
  selectedValue: string;  
  entityType = EntityType;
  timeZoneControl = new FormControl('');
  schedulerScope: 'tenant' | 'customer' | 'customer_user' | 'edge';
  currentDateAndTime: string;
  startTime: string;
  showStartTime: boolean = false;
  campaignOne: FormGroup<{ startdatetime: FormControl<Date>; }>;
  entityForm: any;

  constructor(protected store: Store<AppState>,
    private datePipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public fb: UntypedFormBuilder,
    protected cd: ChangeDetectorRef) {
    const today = new Date();
    const month = today.getMonth();
    const year = today.getFullYear();
    const day = today.getDate();
    const hours = today.getHours();
    const minutes = today.getMinutes();
    this.campaignOne = new FormGroup({
      startdatetime: new FormControl(new Date(year, month, day, hours, minutes))
    });
      console.log(this.data.date);
}

ngOnInit() {
  this.currentDateAndTime = this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss');
  this.entityForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    'event-type': ['', Validators.required],
    'date': ['', Validators.required],
  });
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

  closeScheduler() {
    this.scheduler.nativeElement.style.display = 'none';
  }

  timeZones: { value: string, viewValue: string }[] = [
    { value: 'Pacific/Midway', viewValue: 'Pacific/Midway (UTC-11:00)' },
    { value: 'America/Adak', viewValue: 'America/Adak (UTC-10:00)' },
    { value: 'Pacific/Honolulu', viewValue: 'Pacific/Honolulu (UTC-10:00)' },
    { value: 'America/Anchorage', viewValue: 'America/Anchorage (UTC-09:00)' },
    { value: 'America/Los_Angeles', viewValue: 'America/Los_Angeles (UTC-08:00)' },
    { value: 'America/Denver', viewValue: 'America/Denver (UTC-07:00)' },
    { value: 'America/Chicago', viewValue: 'America/Chicago (UTC-06:00)' },
    { value: 'America/New_York', viewValue: 'America/New_York (UTC-05:00)' },
    { value: 'America/Caracas', viewValue: 'America/Caracas (UTC-04:30)' },
    { value: 'America/Halifax', viewValue: 'America/Halifax (UTC-04:00)' },
    { value: 'America/St_Johns', viewValue: 'America/St_Johns (UTC-03:30)' },
    { value: 'America/Argentina/Buenos_Aires', viewValue: 'America/Argentina/Buenos_Aires (UTC-03:00)' },
    { value: 'America/Noronha', viewValue: 'America/Noronha (UTC-02:00)' },
    { value: 'Atlantic/Azores', viewValue: 'Atlantic/Azores (UTC-01:00)' },
    { value: 'Europe/London', viewValue: 'Europe/London (UTC+00:00)' },
    { value: 'Europe/Berlin', viewValue: 'Europe/Berlin (UTC+01:00)' },
    { value: 'Africa/Johannesburg', viewValue: 'Africa/Johannesburg (UTC+02:00)' },
    { value: 'Europe/Moscow', viewValue: 'Europe/Moscow (UTC+03:00)' },
    { value: 'Asia/Dubai', viewValue: 'Asia/Dubai (UTC+04:00)' },
    { value: 'Asia/Kolkata', viewValue: 'Asia/Kolkata (UTC+05:30)' },
    { value: 'Asia/Kathmandu', viewValue: 'Asia/Kathmandu (UTC+05:45)' },
    { value: 'Asia/Dhaka', viewValue: 'Asia/Dhaka (UTC+06:00)' },
    { value: 'Asia/Jakarta', viewValue: 'Asia/Jakarta (UTC+07:00)' },
    { value: 'Asia/Shanghai', viewValue: 'Asia/Shanghai (UTC+08:00)' },
    { value: 'Asia/Tokyo', viewValue: 'Asia/Tokyo (UTC+09:00)' },
    { value: 'Australia/Darwin', viewValue: 'Australia/Darwin (UTC+09:30)' },
    { value: 'Australia/Sydney', viewValue: 'Australia/Sydney (UTC+10:00)' },
    { value: 'Pacific/Noumea', viewValue: 'Pacific/Noumea (UTC+11:00)' },
    { value: 'Pacific/Auckland', viewValue: 'Pacific/Auckland (UTC+12:00)' }
  ];

  repeats: repeat[] = [
    {value: 'Daily-0', viewValue: 'Daily'},
    {value: 'Every-N-days-1', viewValue: 'Every N days'},
    {value: 'Weekly-2', viewValue: 'Weekly'},
    {value: 'Every-N-week-3', viewValue: 'Every N week'},
    {value: 'Monthly-4', viewValue: 'Monthly'},
    {value: 'yearly-5', viewValue: 'yearly'},
  ];

}
