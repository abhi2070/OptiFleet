/* eslint-disable prefer-arrow/prefer-arrow-functions */
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { GlobalHttpInterceptor } from './interceptors/global-http-interceptor';
import { effects, metaReducers, reducers } from './core.state';
import { environment as env } from '@env/environment';
import {
  MissingTranslationHandler,
  TranslateCompiler,
  TranslateLoader,
  TranslateModule,
  TranslateParser
} from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TbMissingTranslationHandler } from './translate/missing-translate-handler';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DEFAULT_OPTIONS, MatDialogConfig, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslateDefaultCompiler } from '@core/translate/translate-default-compiler';
import { WINDOW_PROVIDERS } from '@core/services/window.service';
import { HotkeyModule } from 'angular2-hotkeys';
import { TranslateDefaultParser } from '@core/translate/translate-default-parser';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/locale/locale.constant-', '.json');
}

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    FlexLayoutModule.withConfig({addFlexToParent: false}),
    MatDialogModule,
    MatButtonModule,
    MatSnackBarModule,

    // ngx-translate
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      },
      missingTranslationHandler: {
        provide: MissingTranslationHandler,
        useClass: TbMissingTranslationHandler
      },
      compiler: {
        provide: TranslateCompiler,
        useClass: TranslateDefaultCompiler
      },
      parser: {
        provide: TranslateParser,
        useClass: TranslateDefaultParser
      }
    }),
    HotkeyModule.forRoot(),

    // ngrx
    StoreModule.forRoot(reducers,
      { metaReducers,
        runtimeChecks: {
          strictStateImmutability: true,
          strictActionImmutability: true,
          strictStateSerializability: true,
          strictActionSerializability: true
        }}
    ),
    EffectsModule.forRoot(effects),
    env.production
      ? []
      : StoreDevtoolsModule.instrument({
        name: env.appTitle
      })
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: GlobalHttpInterceptor,
      multi: true
    },
    {
      provide: MAT_DIALOG_DEFAULT_OPTIONS,
      useValue: {
        ...new MatDialogConfig(),
        restoreFocus: false
      }
    },
    WINDOW_PROVIDERS
  ],
  exports: []
})
export class CoreModule {
}
