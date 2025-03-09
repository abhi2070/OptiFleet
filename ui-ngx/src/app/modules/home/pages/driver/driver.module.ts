import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';
import { DriverRoutingModule } from '@modules/home/pages/driver//driver-routing.module';
import { DriverComponent } from './driver.component';
import { DriverTabsComponent } from './driver-tabs/driver-tabs.component';
import { DriverDetailComponent } from './driver-detail/driver-detail.component';
import { HomeDialogsModule } from '../../dialogs/home-dialogs.module';
import { DriverProfileComponent } from './driver-profile/driver-profile.component';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@NgModule({
  declarations: [
    DriverComponent,
    DriverTabsComponent,
    DriverDetailComponent,
    DriverProfileComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    HomeDialogsModule,
    HomeComponentsModule,
    DriverRoutingModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MatSnackBarModule,
    MatProgressBarModule
  ]
})
export class DriverModule { }
