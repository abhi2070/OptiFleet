

import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { PageComponent } from '@shared/components/page.component';
import { Dashboard } from '@shared/models/dashboard.models';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'tb-api-usage',
  templateUrl: './api-usage.component.html',
  styleUrls: ['./api-usage.component.scss']
})
export class ApiUsageComponent extends PageComponent {

  apiUsageDashboard: Dashboard = this.route.snapshot.data.apiUsageDashboard;

  constructor(protected store: Store<AppState>,
              private route: ActivatedRoute) {
    super(store);
  }
}
