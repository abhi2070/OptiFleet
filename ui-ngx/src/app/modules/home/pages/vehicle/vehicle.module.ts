import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VehicleRoutingModule } from './vehicle-routing.module';
import { VehicleComponent } from './vehicle.component';
import { VehicleTabsComponent } from './vehicle-tabs/vehicle-tabs.component';
import { SharedModule } from '@app/shared/shared.module';
import { HomeComponentsModule } from '../../components/public-api';
import { MatDatetimepickerModule } from '@mat-datetimepicker/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { VehicleDetailComponent } from './vehicle-detail/vehicle-detail.component';
import { VehicleProfileComponent } from './vehicle-profile/vehicle-profile.component';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatChipsModule } from '@angular/material/chips';


@NgModule({
  declarations: [
    VehicleComponent,
    VehicleTabsComponent,
    VehicleDetailComponent,
    VehicleProfileComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    VehicleRoutingModule,
    MatDatetimepickerModule,
    MatDatepickerModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MatSnackBarModule,
    MatProgressBarModule,
    MatChipsModule,
    MatAutocompleteModule,
  ]
})
export class VehicleModule { }
