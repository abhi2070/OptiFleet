import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TripHistoryRoutingModule } from './trip-history-routing.module';
import { SharedModule } from '@app/shared/shared.module';
import { HomeDialogsModule } from '../../dialogs/home-dialogs.module';
import { HomeComponentsModule } from '../../components/home-components.module';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TripHistoryComponent } from './trip-history.component';


@NgModule({
  declarations: [
    TripHistoryComponent
  ],
  imports: [
    CommonModule,
    TripHistoryRoutingModule,
    SharedModule,
    HomeDialogsModule,
    HomeComponentsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MatSnackBarModule,
    MatProgressBarModule
  ]
})
export class TripHistoryModule { }
