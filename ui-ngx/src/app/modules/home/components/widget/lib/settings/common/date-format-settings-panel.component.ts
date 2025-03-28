

import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { DateFormatSettings } from '@shared/models/widget-settings.models';
import { TbPopoverComponent } from '@shared/components/popover.component';
import { UntypedFormControl, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'tb-date-format-settings-panel',
  templateUrl: './date-format-settings-panel.component.html',
  providers: [],
  styleUrls: ['./date-format-settings-panel.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DateFormatSettingsPanelComponent extends PageComponent implements OnInit {

  @Input()
  dateFormat: DateFormatSettings;

  @Input()
  popover: TbPopoverComponent<DateFormatSettingsPanelComponent>;

  @Output()
  dateFormatApplied = new EventEmitter<DateFormatSettings>();

  dateFormatFormControl: UntypedFormControl;

  previewText = '';

  constructor(private date: DatePipe,
              protected store: Store<AppState>) {
    super(store);
  }

  ngOnInit(): void {
    this.dateFormatFormControl = new UntypedFormControl(this.dateFormat.format, [Validators.required]);
    this.dateFormatFormControl.valueChanges.subscribe((value: string) => {
      this.previewText = this.date.transform(Date.now(), value);
    });
    this.previewText = this.date.transform(Date.now(), this.dateFormat.format);
  }

  cancel() {
    this.popover?.hide();
  }

  applyDateFormat() {
    this.dateFormat.format = this.dateFormatFormControl.value;
    this.dateFormatApplied.emit(this.dateFormat);
  }

}
