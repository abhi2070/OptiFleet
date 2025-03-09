

import { Action } from '@ngrx/store';
import { AuthUser, User } from '@shared/models/user.model';
import { AuthPayload } from '@core/auth/auth.models';
import { UserSettings } from '@shared/models/user-settings.models';

export enum AuthActionTypes {
  AUTHENTICATED = '[Auth] Authenticated',
  UNAUTHENTICATED = '[Auth] Unauthenticated',
  LOAD_USER = '[Auth] Load User',
  UPDATE_USER_DETAILS = '[Auth] Update User Details',
  UPDATE_AUTH_USER = '[Auth] Update Auth User',
  UPDATE_LAST_PUBLIC_DASHBOARD_ID = '[Auth] Update Last Public Dashboard Id',
  UPDATE_HAS_REPOSITORY = '[Auth] Change Has Repository',
  UPDATE_OPENED_MENU_SECTION = '[Preferences] Update Opened Menu Section',
  PUT_USER_SETTINGS = '[Preferences] Put user settings',
  DELETE_USER_SETTINGS = '[Preferences] Delete user settings',
}

export class ActionAuthAuthenticated implements Action {
  readonly type = AuthActionTypes.AUTHENTICATED;

  constructor(readonly payload: AuthPayload) {}
}

export class ActionAuthUnauthenticated implements Action {
  readonly type = AuthActionTypes.UNAUTHENTICATED;
}

export class ActionAuthLoadUser implements Action {
  readonly type = AuthActionTypes.LOAD_USER;

  constructor(readonly payload: { isUserLoaded: boolean }) {}
}

export class ActionAuthUpdateUserDetails implements Action {
  readonly type = AuthActionTypes.UPDATE_USER_DETAILS;

  constructor(readonly payload: { userDetails: User }) {}
}

export class ActionAuthUpdateAuthUser implements Action {
  readonly type = AuthActionTypes.UPDATE_AUTH_USER;

  constructor(readonly payload: Partial<AuthUser>) {}
}

export class ActionAuthUpdateLastPublicDashboardId implements Action {
  readonly type = AuthActionTypes.UPDATE_LAST_PUBLIC_DASHBOARD_ID;

  constructor(readonly payload: { lastPublicDashboardId: string }) {}
}

export class ActionAuthUpdateHasRepository implements Action {
  readonly type = AuthActionTypes.UPDATE_HAS_REPOSITORY;

  constructor(readonly payload: { hasRepository: boolean }) {}
}

export class ActionPreferencesUpdateOpenedMenuSection implements Action {
  readonly type = AuthActionTypes.UPDATE_OPENED_MENU_SECTION;

  constructor(readonly payload: { path: string; opened: boolean }) {}
}

export class ActionPreferencesPutUserSettings implements Action {
  readonly type = AuthActionTypes.PUT_USER_SETTINGS;

  constructor(readonly payload: Partial<UserSettings>) {}
}

export class ActionPreferencesDeleteUserSettings implements Action {
  readonly type = AuthActionTypes.DELETE_USER_SETTINGS;

  constructor(readonly payload: Array<NestedKeyOf<UserSettings>>) {}
}

export type AuthActions = ActionAuthAuthenticated | ActionAuthUnauthenticated |
  ActionAuthLoadUser | ActionAuthUpdateUserDetails | ActionAuthUpdateLastPublicDashboardId | ActionAuthUpdateHasRepository |
  ActionPreferencesUpdateOpenedMenuSection | ActionPreferencesPutUserSettings | ActionPreferencesDeleteUserSettings |
  ActionAuthUpdateAuthUser;
