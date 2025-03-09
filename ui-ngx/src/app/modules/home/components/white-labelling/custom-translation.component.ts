import { Component } from '@angular/core';

@Component({
  selector: 'tb-custom-translation',
  templateUrl: './custom-translation.component.html',
  styleUrls: ['./custom-translation.component.scss']
})
export class CustomTranslationComponent {
  isDisabledDelete: boolean = true;

  save() {
    console.log('Delete action triggered');
    // Implement the delete logic here
  }
}
