import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';
import { SchedulerRoutingModule } from './scheduler-routing.module';
import { SchedulerComponent } from './scheduler.component';
import { SchedulerCalendarComponent } from './scheduler-calendar/scheduler-calendar.component';
import { SchedulerCalendarDialogComponent } from './scheduler-calendar-dialog/scheduler-calendar-dialog.component';
import { MatDatetimepickerModule } from '@mat-datetimepicker/core';
import { MatDatepickerModule } from '@angular/material/datepicker';

@NgModule({
  declarations: [
    SchedulerComponent,
    SchedulerCalendarComponent,
    SchedulerCalendarDialogComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SchedulerRoutingModule,
    MatDatetimepickerModule,
    MatDatepickerModule,

  ]
})
export class SchedulerModule { }
