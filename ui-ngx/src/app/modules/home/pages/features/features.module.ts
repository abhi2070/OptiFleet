import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { FeaturesRoutingModule } from '@home/pages/features/features-routing.module';
import { MatCardModule } from '@angular/material/card';

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    SharedModule,
    FeaturesRoutingModule,
    MatCardModule
  ]
})
export class FeaturesModule { }
