

import { ChangeDetectorRef, Component, Input, NgZone, OnDestroy, OnInit } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { TbPopoverComponent } from '@shared/components/popover.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { Notification, NotificationRequest } from '@shared/models/notification.models';
import { NotificationWebsocketService } from '@core/ws/notification-websocket.service';
import { BehaviorSubject, Observable, ReplaySubject, Subscription } from 'rxjs';
import { map, share, skip, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { NotificationSubscriber } from '@shared/models/telemetry/telemetry.models';

@Component({
  selector: 'tb-show-notification-popover',
  templateUrl: './show-notification-popover.component.html',
  styleUrls: []
})
export class ShowNotificationPopoverComponent extends PageComponent implements OnDestroy, OnInit {

  @Input()
  onClose: () => void;

  @Input()
  counter: BehaviorSubject<number>;

  @Input()
  popoverComponent: TbPopoverComponent;

  private notificationSubscriber: NotificationSubscriber;
  private notificationCountSubscriber: Subscription;

  notifications$: Observable<Notification[]>;
  loadNotification = false;

  constructor(protected store: Store<AppState>,
              private notificationWsService: NotificationWebsocketService,
              private zone: NgZone,
              private cd: ChangeDetectorRef,
              private router: Router) {
    super(store);
  }

  ngOnInit() {
    this.notificationSubscriber = NotificationSubscriber.createNotificationsSubscription(this.notificationWsService, this.zone, 6);
    this.notifications$ = this.notificationSubscriber.notifications$.pipe(
      map(value => {
        if (Array.isArray(value)) {
          this.loadNotification = true;
          return value;
        }
        return [];
      }),
      share({
        connector: () => new ReplaySubject(1)
      }),
      tap(() => setTimeout(() => this.cd.markForCheck()))
    );
    this.notificationCountSubscriber = this.notificationSubscriber.notificationCount$.pipe(
      skip(1),
    ).subscribe(value => this.counter.next(value));
    this.notificationSubscriber.subscribe();
  }

  ngOnDestroy() {
    super.ngOnDestroy();
    this.notificationCountSubscriber.unsubscribe();
    this.notificationSubscriber.unsubscribe();
    this.onClose();
  }

  markAsRead(id: string) {
    const cmd = NotificationSubscriber.createMarkAsReadCommand(this.notificationWsService, [id]);
    cmd.subscribe();
  }

  markAsAllRead($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    const cmd = NotificationSubscriber.createMarkAllAsReadCommand(this.notificationWsService);
    cmd.subscribe();
  }

  viewAll($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.onClose();
    this.router.navigateByUrl(this.router.parseUrl('/notification/inbox')).then(() => {});
  }

  trackById(index: number, item: NotificationRequest): string {
    return item.id.id;
  }
}
