
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { CoreModule } from '@core/core.module';
import { LoginModule } from '@modules/login/login.module';
import { HomeModule } from '@home/home.module';

import { AppComponent } from './app.component';
import { DashboardRoutingModule } from '@modules/dashboard/dashboard-routing.module';
import { RouterModule, Routes } from '@angular/router';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { SchedulerModule } from './modules/home/pages/scheduler/scheduler.module';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';


const routes: Routes = [
  {
    path: '**',
    redirectTo: 'home'
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)],
  exports: [RouterModule],
  declarations: [

  ]
})
export class PageNotFoundRoutingModule { }


@NgModule({

  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CoreModule,
    LoginModule,
    HomeModule,
    DashboardRoutingModule,
    PageNotFoundRoutingModule,
    SchedulerModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule,
    CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory })
  ],

  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
