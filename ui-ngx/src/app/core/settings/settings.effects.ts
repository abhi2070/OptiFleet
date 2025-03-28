

import { ActivationEnd, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { TranslateService } from '@ngx-translate/core';
import { merge } from 'rxjs';
import { distinctUntilChanged, filter, map, tap, withLatestFrom } from 'rxjs/operators';

import { SettingsActions, SettingsActionTypes, } from './settings.actions';
import { selectSettingsState } from './settings.selectors';
import { AppState } from '@app/core/core.state';
import { LocalStorageService } from '@app/core/local-storage/local-storage.service';
import { TitleService } from '@app/core/services/title.service';
import { updateUserLang } from '@app/core/settings/settings.utils';
import { AuthService } from '@core/auth/auth.service';
import { UtilsService } from '@core/services/utils.service';
import { getCurrentAuthUser } from '@core/auth/auth.selectors';
import { ActionAuthUpdateLastPublicDashboardId } from '../auth/auth.actions';

export const SETTINGS_KEY = 'SETTINGS';

@Injectable()
export class SettingsEffects {
  constructor(
    private actions$: Actions<SettingsActions>,
    private store: Store<AppState>,
    private authService: AuthService,
    private utils: UtilsService,
    private router: Router,
    private localStorageService: LocalStorageService,
    private titleService: TitleService,
    private translate: TranslateService
  ) {
  }

  
  persistSettings = createEffect(() => this.actions$.pipe(
    ofType(
      SettingsActionTypes.CHANGE_LANGUAGE,
    ),
    withLatestFrom(this.store.pipe(select(selectSettingsState))),
    tap(([action, settings]) =>
      this.localStorageService.setItem(SETTINGS_KEY, settings)
    )
  ), {dispatch: false});

  
  setTranslateServiceLanguage = createEffect(() => this.store.pipe(
    select(selectSettingsState),
    map(settings => settings.userLang),
    distinctUntilChanged(),
    tap(userLang => updateUserLang(this.translate, userLang))
  ), {dispatch: false});

  
  setTitle = createEffect(() => merge(
    this.actions$.pipe(ofType(SettingsActionTypes.CHANGE_LANGUAGE)),
    this.router.events.pipe(filter(event => event instanceof ActivationEnd))
  ).pipe(
    tap(() => {
      this.titleService.setTitle(
        this.router.routerState.snapshot.root,
        this.translate
      );
    })
  ), {dispatch: false});

  
  setPublicId = createEffect(() => merge(
    this.router.events.pipe(filter(event => event instanceof ActivationEnd))
  ).pipe(
    tap((event) => {
      const authUser = getCurrentAuthUser(this.store);
      const snapshot = (event as ActivationEnd).snapshot;
      if (authUser && authUser.isPublic && snapshot.url && snapshot.url.length
          && snapshot.url[0].path === 'dashboard') {
        this.utils.updateQueryParam('publicId', authUser.sub);
        this.store.dispatch(new ActionAuthUpdateLastPublicDashboardId(
          { lastPublicDashboardId: snapshot.params.dashboardId}));
      }
    })
  ), {dispatch: false});
}
