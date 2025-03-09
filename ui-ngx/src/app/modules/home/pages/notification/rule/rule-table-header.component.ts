

import { Component } from '@angular/core';
import { EntityTableHeaderComponent } from '@home/components/entity/entity-table-header.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { NotificationRule } from '@shared/models/notification.models';

@Component({
  selector: 'tb-rule-table-header',
  templateUrl: './rule-table-header.component.html',
  styleUrls: ['rule-table-header.component.scss']
})
export class RuleTableHeaderComponent extends EntityTableHeaderComponent<NotificationRule> {

  constructor(protected store: Store<AppState>) {
    super(store);
  }

  createTarget($event: Event) {
    this.entitiesTableConfig.onEntityAction({event: $event, action: 'add', entity: null});
  }

}
