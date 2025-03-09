

import { Component } from '@angular/core';
import { EntityTableHeaderComponent } from '@home/components/entity/entity-table-header.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { NotificationTemplate } from '@shared/models/notification.models';

@Component({
  selector: 'tb-template-table-header',
  templateUrl: './template-table-header.component.html',
  styleUrls: ['template-table-header.component.scss']
})
export class TemplateTableHeaderComponent extends EntityTableHeaderComponent<NotificationTemplate> {

  constructor(protected store: Store<AppState>) {
    super(store);
  }

  createTemplate($event: Event) {
    this.entitiesTableConfig.onEntityAction({event: $event, action: 'add', entity: null});
  }

}
