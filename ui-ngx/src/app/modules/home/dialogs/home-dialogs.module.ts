

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';
import { AssignToCustomerDialogComponent } from '@modules/home/dialogs/assign-to-customer-dialog.component';
import { AddEntitiesToCustomerDialogComponent } from '@modules/home/dialogs/add-entities-to-customer-dialog.component';
import { HomeDialogsService } from './home-dialogs.service';
import { AddEntitiesToEdgeDialogComponent } from '@home/dialogs/add-entities-to-edge-dialog.component';
import { UpdateDialogComponent } from './update-dialog.component';
import { MatDatetimepickerModule } from '@mat-datetimepicker/core';
import { MatDatepickerModule } from '@angular/material/datepicker';


@NgModule({
  declarations:
  [
    AssignToCustomerDialogComponent,
    AddEntitiesToCustomerDialogComponent,
    AddEntitiesToEdgeDialogComponent,
    UpdateDialogComponent,

  ],
  imports: [
    CommonModule,
    SharedModule,
    MatDatetimepickerModule,
    MatDatepickerModule
  ],
  exports: [
    AssignToCustomerDialogComponent,
    AddEntitiesToCustomerDialogComponent,
    AddEntitiesToEdgeDialogComponent,
    UpdateDialogComponent
  ],
  providers: [
    HomeDialogsService
  ]
})
export class HomeDialogsModule { }
