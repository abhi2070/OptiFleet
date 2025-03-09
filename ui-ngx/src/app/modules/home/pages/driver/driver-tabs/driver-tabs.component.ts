import { Component } from '@angular/core';
import { AppState } from '@app/core/public-api';
import { EntityTabsComponent } from '@app/modules/home/components/entity/entity-tabs.component';
import { DriverInfo } from '@app/shared/models/driver.model';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-driver-tabs',
  templateUrl: './driver-tabs.component.html',
  styleUrls: ['./driver-tabs.component.scss']
})
export class DriverTabsComponent extends EntityTabsComponent<DriverInfo> {

  constructor(protected store: Store<AppState>) {
      super(store);
    }

    ngOnInit() {
      super.ngOnInit();
    }
}
