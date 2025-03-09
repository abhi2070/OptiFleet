import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WhiteLabellingComponent } from '../../components/white-labelling.component';
import { SharedModule } from '@app/shared/shared.module';
import { HomeComponentsModule } from '../../components/public-api';
import { WhiteLabellingRoutingModule } from './white-labelling-routing.module';
import { MatCardModule } from '@angular/material/card';
import { GeneralComponent } from '../../components/white-labelling/general.component';
import { LoginComponent } from '../../components/white-labelling/login.component';
import { MailTemplatesComponent } from '../../components/white-labelling/mail-templates.component';
import { CustomTranslationComponent } from '../../components/white-labelling/custom-translation.component';
import { CustomMenuComponent } from '../../components/white-labelling/custom-menu.component';




@NgModule({
  declarations: [
    WhiteLabellingComponent,
    GeneralComponent,
    LoginComponent,
    MailTemplatesComponent,
    CustomTranslationComponent,
    CustomMenuComponent

  ],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    WhiteLabellingRoutingModule,
    MatCardModule,
  ]
})
export class WhiteLabellingModule { }
