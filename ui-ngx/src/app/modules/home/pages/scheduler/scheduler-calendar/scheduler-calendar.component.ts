import { ChangeDetectionStrategy, Component, Inject, TemplateRef, ViewChild } from '@angular/core';
import { CalendarEventAction, CalendarEventTimesChangedEvent, CalendarView } from 'angular-calendar';
import { startOfDay, endOfDay, subDays, addDays, startOfWeek, endOfWeek, startOfMonth, endOfMonth, addMonths, subMonths, eachDayOfInterval, getDay, isSameDay } from 'date-fns';
import { MatDialog } from '@angular/material/dialog';
import{SchedulerComponent} from '../scheduler.component'
import { SchedulerCalendarDialogComponent } from '../scheduler-calendar-dialog/scheduler-calendar-dialog.component';


interface CalendarEvent {
  start: Date;
  end?: Date;
  title: string;
  color: {
    primary: string;
    secondary: string;
  };
}

@Component({
  selector: 'tb-scheduler-calendar',
  templateUrl: './scheduler-calendar.component.html',
  styleUrls: ['./scheduler-calendar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SchedulerCalendarComponent {

  view: string = 'month';
  CalendarView = CalendarView;
  viewDate: Date = new Date();

  events: CalendarEvent[] = [
    {
      start: subDays(new Date(), 1),
      end: addDays(new Date(), 1),
      title: 'A 3 day event',
      color: {
        primary: '#1e90ff',
        secondary: '#D1E8FF'
      }
    }
  ];

  constructor(public dialog: MatDialog) {}

 nextClicked(): void {
  switch (this.view) {
    case 'month':
      this.viewDate = addMonths(this.viewDate, 1);
      break;
    case 'week':
    case 'agendaWeek':
    case 'listWeek':
      this.viewDate = addDays(this.viewDate, 7);
      break;
    case 'day':
    case 'agendaDay':
    case 'listDay':
      this.viewDate = addDays(this.viewDate, 1);
      break;
    case 'listMonth':
      this.viewDate = addMonths(this.viewDate, 1);
      break;
    case 'listYear':
      this.viewDate = addMonths(this.viewDate, 12);
      break;
  }
}

previousClicked(): void {
  switch (this.view) {
    case 'month':
      this.viewDate = subMonths(this.viewDate, 1);
      break;
    case 'week':
    case 'agendaWeek':
    case 'listWeek':
      this.viewDate = subDays(this.viewDate, 7);
      break;
    case 'day':
    case 'agendaDay':
    case 'listDay':
      this.viewDate = subDays(this.viewDate, 1);
      break;
    case 'listMonth':
      this.viewDate = subMonths(this.viewDate, 1);
      break;
    case 'listYear':
      this.viewDate = subMonths(this.viewDate, 12);
      break;
  }
}

  goToToday(): void {
    this.viewDate = new Date();
  }

  openSchedulerDialog(day: Date): void {
    this.dialog.open(SchedulerCalendarDialogComponent, {
      data: { date: day },
      width: '80%',
    maxWidth: '100%',
    height: 'auto'
    });
  }

  get monthDays(): (Date | null)[] {
    const startOfMonthDate = startOfMonth(this.viewDate);
    const endOfMonthDate = endOfMonth(this.viewDate);
    const startDayOfWeek = getDay(startOfMonthDate); // 0 = Sunday, 1 = Monday, ..., 6 = Saturday
    const daysInMonth = eachDayOfInterval({ start: startOfMonthDate, end: endOfMonthDate });
    const placeholders = Array.from({ length: startDayOfWeek }, () => null);
    return [...placeholders, ...daysInMonth];
  }

  get weekDays(): string[] {
    return ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  }

  getCurrentWeekDays(): Date[] {
    const startOfWeekDate = startOfWeek(this.viewDate);
    const endOfWeekDate = endOfWeek(this.viewDate);
    return eachDayOfInterval({ start: startOfWeekDate, end: endOfWeekDate });
  }



  getEventsForDay(day: Date): CalendarEvent[] {
    return this.events.filter(event =>
      isSameDay(day, event.start) || (event.end && isSameDay(day, event.end))
    );
  }

  isToday(date: Date | null): boolean {
    if (!date) return false;
    return isSameDay(date, new Date());
  }

  getDaysOfWeek(date: Date): Date[] {
    const start = startOfWeek(date, { weekStartsOn: 0 });
    const end = endOfWeek(date, { weekStartsOn: 0 });
    return eachDayOfInterval({ start, end });
  }
}
