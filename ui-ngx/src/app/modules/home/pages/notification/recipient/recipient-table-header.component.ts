

import { Component } from '@angular/core';
import { EntityTableHeaderComponent } from '@home/components/entity/entity-table-header.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { NotificationTarget } from '@shared/models/notification.models';

@Component({
  selector: 'tb-recipient-table-header',
  templateUrl: './recipient-table-header.component.html',
  styleUrls: ['recipient-table-header.component.scss']
})
export class RecipientTableHeaderComponent extends EntityTableHeaderComponent<NotificationTarget> {

  constructor(protected store: Store<AppState>) {
    super(store);
  }

  createTarget($event: Event) {
    this.entitiesTableConfig.onEntityAction({event: $event, action: 'add', entity: null});
  }

}
