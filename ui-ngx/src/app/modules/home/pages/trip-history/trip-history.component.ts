import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { AppState } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { TripHistoryInfo } from '@app/shared/models/trip-history.model';
import { EntityType } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

export interface Trip {
  selected?: boolean;
  name: string;
  startDate: Date;
  endDate: Date;
  duration: number;
  vehicleNumber: string;
  status: string;
}

const TRIP_DATA: Trip[] = [
  { name: 'Trip A', startDate: new Date(), endDate: new Date(), duration: 120, vehicleNumber: 'MH 12 AB 1234', status: 'Ongoing' },
  { name: 'Trip B', startDate: new Date(), endDate: new Date(), duration: 150, vehicleNumber: 'MH 14 XY 5678', status: 'Completed' }
];

@Component({
  selector: 'tb-trip-history',
  templateUrl: './trip-history.component.html',
  styleUrls: ['./trip-history.component.scss']
})
export class TripHistoryComponent implements AfterViewInit{
  displayedColumns: string[] = ['select', 'name', 'startDate', 'endDate', 'duration', 'vehicleNumber', 'status'];
  dataSource: MatTableDataSource<any>;
  selection = new SelectionModel<any>(true, []);

  pageSize = 10;
  pageIndex = 0;
  totalItems = 0;
  totalPages = 0;

  @ViewChild(MatSort) sort: MatSort;

  constructor() {
    this.dataSource = new MatTableDataSource([]);
  }

  ngOnInit() {
    this.loadData();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }

  get startIndex(): number {
    return this.pageIndex * this.pageSize + 1;
  }

  get endIndex(): number {
    return Math.min((this.pageIndex + 1) * this.pageSize, this.totalItems);
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
        this.selection.clear() :
        this.dataSource.data.forEach(row => this.selection.select(row));
  }

  refreshTable() {
    this.loadData();
  }

  loadData() {
    // Implement your data loading logic here
  }

  onPageSizeChange(event: any) {
    this.pageSize = event.value;
    this.pageIndex = 0;
    this.loadData();
  }

  firstPage() {
    this.pageIndex = 0;
    this.loadData();
  }

  previousPage() {
    this.pageIndex--;
    this.loadData();
  }

  nextPage() {
    this.pageIndex++;
    this.loadData();
  }

  lastPage() {
    this.pageIndex = this.totalPages - 1;
    this.loadData();
  }

  
}