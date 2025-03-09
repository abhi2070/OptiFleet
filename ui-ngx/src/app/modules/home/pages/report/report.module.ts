import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { ReportRoutingModule } from './report-routing.module';
import { EquipmentUtilizationComponent } from '../../components/reports/equipment-utilization.component';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { ReportFilterComponent } from '../../components/reports/report-filter.component';
import { MatDialogModule } from '@angular/material/dialog';
import { AddReportDialogComponent } from '../../components/reports/add-report-dialog.component';

@NgModule({
  declarations: [
    EquipmentUtilizationComponent,
    ReportFilterComponent,
    AddReportDialogComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReportRoutingModule,
    MatTableModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatDialogModule
  ],
  providers: []
})
export class ReportModule { }
