import { Component } from '@angular/core';

@Component({
  selector: 'tb-custom-menu',
  templateUrl: './custom-menu.component.html',
  styleUrls: ['./custom-menu.component.scss']
})
export class CustomMenuComponent {
  isDisabledDelete: boolean = true;

  save() {
    console.log('Delete action triggered');
    // Implement the delete logic here
  }
}
