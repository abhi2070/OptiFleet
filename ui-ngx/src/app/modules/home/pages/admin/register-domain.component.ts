import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { Store } from '@ngrx/store';
import { PageComponent } from '@shared/components/page.component';

@Component({
  selector: 'tb-register-domain',
  templateUrl: './register-domain.component.html',
  styleUrls: ['./register-domain.component.scss']
})
export class RegisterDomainComponent extends PageComponent {

  showMainLoadingBar = false;

  constructor(protected store: Store<AppState>) {
    super(store);
  }
}
