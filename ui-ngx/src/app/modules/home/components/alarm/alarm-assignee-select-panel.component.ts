

import {
  AfterViewInit,
  Component,
  ElementRef,
  Inject,
  InjectionToken,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable, of, Subject } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, share, switchMap, takeUntil, } from 'rxjs/operators';
import { User, UserEmailInfo } from '@shared/models/user.model';
import { TranslateService } from '@ngx-translate/core';
import { UserService } from '@core/http/user.service';
import { PageLink } from '@shared/models/page/page-link';
import { Direction } from '@shared/models/page/sort-order';
import { emptyPageData } from '@shared/models/page/page-data';
import { OverlayRef } from '@angular/cdk/overlay';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { UtilsService } from '@core/services/utils.service';
import { AlarmAssigneeOption } from '@shared/models/alarm.models';

export const ALARM_ASSIGNEE_SELECT_PANEL_DATA = new InjectionToken<any>('AlarmAssigneeSelectPanelData');

export interface AlarmAssigneeSelectPanelData {
  assigneeId?: string;
  assigneeOption?: AlarmAssigneeOption;
  userMode?: boolean;
}

@Component({
  selector: 'tb-alarm-assignee-select-panel',
  templateUrl: './alarm-assignee-panel.component.html',
  styleUrls: ['./alarm-assignee-panel.component.scss']
})
export class AlarmAssigneeSelectPanelComponent implements  OnInit, AfterViewInit, OnDestroy {

  assigneeOptions = AlarmAssigneeOption;

  private dirty = false;

  assigneeId?: string;
  assigneeOption?: AlarmAssigneeOption;

  assigneeNotSetText = 'alarm.assignee-not-set';
  assignedToCurrentUserText = this.data.userMode ? 'alarm.assigned-to-me' : 'alarm.assigned-to-current-user';

  selectUserFormGroup: FormGroup;

  @ViewChild('userInput', {static: true}) userInput: ElementRef;

  filteredUsers: Observable<Array<UserEmailInfo>>;

  searchText = '';

  userSelected = false;

  result?: UserEmailInfo;
  optionResult?: AlarmAssigneeOption;

  get displayAssigneeNotSet(): boolean {
    return this.assigneeOption !== AlarmAssigneeOption.noAssignee;
  }

  get displayAssignedToCurrentUser(): boolean {
    return this.assigneeOption !== AlarmAssigneeOption.currentUser;
  }

  private destroy$ = new Subject<void>();

  constructor(@Inject(ALARM_ASSIGNEE_SELECT_PANEL_DATA) public data: AlarmAssigneeSelectPanelData,
              public overlayRef: OverlayRef,
              public translate: TranslateService,
              private userService: UserService,
              private fb: FormBuilder,
              private utilsService: UtilsService) {
    this.assigneeId = data.assigneeId;
    this.assigneeOption = data.assigneeOption;
    this.selectUserFormGroup = this.fb.group({
      user: [null]
    });
  }

  ngOnInit() {
    this.filteredUsers = this.selectUserFormGroup.get('user').valueChanges
      .pipe(
        debounceTime(150),
        map(value => value ? (typeof value === 'string' ? value : '') : ''),
        distinctUntilChanged(),
        switchMap(name => this.fetchUsers(name)),
        share(),
        takeUntil(this.destroy$)
      );
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.userInput.nativeElement.focus();
    }, 0);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  displayUserFn(user?: User): string | undefined {
    return user ? user.email : undefined;
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    this.clear();
    this.userSelected = true;
    if (event.option.value?.id) {
      this.result = event.option.value;
    } else {
      this.optionResult = event.option.value;
    }
    this.overlayRef.dispose();
  }

  fetchUsers(searchText?: string): Observable<Array<UserEmailInfo>> {
    this.searchText = searchText;
    const pageLink = new PageLink(50, 0, searchText, {
      property: 'email',
      direction: Direction.ASC
    });
    return this.userService.findUsersByQuery(pageLink, {ignoreLoading: true})
      .pipe(
        catchError(() => of(emptyPageData<UserEmailInfo>())),
        map(pageData => pageData.data)
      );
  }

  onFocus(): void {
    if (!this.dirty) {
      this.selectUserFormGroup.get('user').updateValueAndValidity({onlySelf: true});
      this.dirty = true;
    }
  }

  clear() {
    this.selectUserFormGroup.get('user').patchValue('', {emitEvent: true});
    setTimeout(() => {
      this.userInput.nativeElement.blur();
      this.userInput.nativeElement.focus();
    }, 0);
  }

  getUserDisplayName(entity: UserEmailInfo) {
    let displayName = '';
    if ((entity.firstName && entity.firstName.length > 0) ||
      (entity.lastName && entity.lastName.length > 0)) {
      if (entity.firstName) {
        displayName += entity.firstName;
      }
      if (entity.lastName) {
        if (displayName.length > 0) {
          displayName += ' ';
        }
        displayName += entity.lastName;
      }
    } else {
      displayName = entity.email;
    }
    return displayName;
  }

  getUserInitials(entity: UserEmailInfo): string {
    let initials = '';
    if (entity.firstName && entity.firstName.length ||
      entity.lastName && entity.lastName.length) {
      if (entity.firstName) {
        initials += entity.firstName.charAt(0);
      }
      if (entity.lastName) {
        initials += entity.lastName.charAt(0);
      }
    } else {
      initials += entity.email.charAt(0);
    }
    return initials.toUpperCase();
  }

  getFullName(entity: UserEmailInfo): string {
    let fullName = '';
    if ((entity.firstName && entity.firstName.length > 0) ||
      (entity.lastName && entity.lastName.length > 0)) {
      if (entity.firstName) {
        fullName += entity.firstName;
      }
      if (entity.lastName) {
        if (fullName.length > 0) {
          fullName += ' ';
        }
        fullName += entity.lastName;
      }
    }
    return fullName;
  }

  getAvatarBgColor(entity: UserEmailInfo) {
    return this.utilsService.stringToHslColor(this.getUserDisplayName(entity), 40, 60);
  }

}
