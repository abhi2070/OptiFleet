

import {
  AfterViewInit,
  ChangeDetectorRef,
  Component, EventEmitter,
  Input,
  OnDestroy,
  OnInit, Output,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { EntityId } from '@shared/models/id/entity-id';
import { EntitiesTableComponent } from '@home/components/entity/entities-table.component';
import { EventTableConfig } from './event-table-config';
import { EventService } from '@core/http/event.service';
import { DialogService } from '@core/services/dialog.service';
import { DebugEventType, EventBody, EventType } from '@shared/models/event.models';
import { Overlay } from '@angular/cdk/overlay';
import { Subscription } from 'rxjs';
import { isNotEmptyStr } from '@core/utils';

@Component({
  selector: 'tb-event-table',
  templateUrl: './event-table.component.html',
  styleUrls: ['./event-table.component.scss']
})
export class EventTableComponent implements OnInit, AfterViewInit, OnDestroy {

  @Input()
  tenantId: string;

  @Input()
  defaultEventType: EventType | DebugEventType;

  @Input()
  disabledEventTypes: Array<EventType | DebugEventType>;

  @Input()
  debugEventTypes: Array<DebugEventType>;

  activeValue = false;
  dirtyValue = false;
  entityIdValue: EntityId;

  get active(): boolean {
    return this.activeValue;
  }

  @Input()
  set active(active: boolean) {
    if (this.activeValue !== active) {
      this.activeValue = active;
      if (this.activeValue && this.dirtyValue) {
        this.dirtyValue = false;
        this.entitiesTable.updateData();
      }
    }
  }

  @Input()
  set entityId(entityId: EntityId) {
    this.entityIdValue = entityId;
    if (this.eventTableConfig && this.eventTableConfig.entityId !== entityId) {
      this.eventTableConfig.eventType = this.defaultEventType;
      this.eventTableConfig.entityId = entityId;
      this.entitiesTable.resetSortAndFilter(this.activeValue);
      if (!this.activeValue) {
        this.dirtyValue = true;
      }
    }
  }

  private functionTestButtonLabelValue: string;

  get functionTestButtonLabel(): string {
    return this.functionTestButtonLabelValue;
  }

  @Input()
  set functionTestButtonLabel(value: string) {
    if (isNotEmptyStr(value)) {
      this.functionTestButtonLabelValue = value;
    } else {
      this.functionTestButtonLabelValue = '';
    }
    if (this.eventTableConfig) {
      this.eventTableConfig.testButtonLabel = this.functionTestButtonLabel;
      this.eventTableConfig.updateCellAction();
    }
  }

  @Output()
  debugEventSelected = new EventEmitter<EventBody>();

  @ViewChild(EntitiesTableComponent, {static: true}) entitiesTable: EntitiesTableComponent;

  eventTableConfig: EventTableConfig;

  private isEmptyData$: Subscription;

  constructor(private eventService: EventService,
              private dialogService: DialogService,
              private translate: TranslateService,
              private datePipe: DatePipe,
              private dialog: MatDialog,
              private overlay: Overlay,
              private viewContainerRef: ViewContainerRef,
              private cd: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.dirtyValue = !this.activeValue;
    this.eventTableConfig = new EventTableConfig(
      this.eventService,
      this.dialogService,
      this.translate,
      this.datePipe,
      this.dialog,
      this.entityIdValue,
      this.tenantId,
      this.defaultEventType,
      this.disabledEventTypes,
      this.debugEventTypes,
      this.overlay,
      this.viewContainerRef,
      this.cd,
      this.functionTestButtonLabel,
      this.debugEventSelected
    );
  }

  ngAfterViewInit() {
    this.isEmptyData$ = this.entitiesTable.dataSource.isEmpty().subscribe(value => this.eventTableConfig.hideClearEventAction = value);
  }

  ngOnDestroy() {
    if (this.isEmptyData$) {
      this.isEmptyData$.unsubscribe();
    }
  }

}
