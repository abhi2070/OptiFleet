import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import { HomeComponentsModule } from '../../components/public-api';
import { TripRoutingModule } from './trip-routing.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDialogModule } from '@angular/material/dialog';
import { TripComponent } from './trip.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { TripDialogComponent } from './trip-dialog/trip-dialog.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDatetimepickerModule } from '@mat-datetimepicker/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';
import { MapDialogComponent } from '../../components/reports/map-dialog/map-dialog.component';
import { MatListModule } from '@angular/material/list';
import { GoogleMapsModule } from '@angular/google-maps';

@NgModule({
  declarations: [TripComponent, TripDialogComponent,MapDialogComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HomeComponentsModule,
    TripRoutingModule,
    FlexLayoutModule,
    TranslateModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatTooltipModule,
    MatAutocompleteModule,
    MatTabsModule,
    MatDialogModule,
    MatSidenavModule,
    MatDatetimepickerModule,
    MatDatepickerModule,
    MatExpansionModule,
    MatToolbarModule,
    MatSelectModule,
    MatProgressBarModule,
    MatListModule,
    GoogleMapsModule
  ]
})
export class TripModule { }
