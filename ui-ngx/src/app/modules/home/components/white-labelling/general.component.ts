import { Component } from '@angular/core';
import { PageComponent } from '@app/shared/public-api';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'tb-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.scss']
})
export class GeneralComponent{



  value: string = '';

  isLoading$ = new BehaviorSubject<boolean>(false); // Define and initialize isLoading$

  isDisabledDelete: boolean = true;
  isDisabledPreview: boolean = true;  // Define the isDisabledDelete property

  // Method to handle delete action
  save() {
    console.log('Delete action triggered');
    // Implement the delete logic here
  }
  preview() {
    console.log('Delete action triggered');
    // Implement the delete logic here
  }


  constructor() {
    // Simulate loading state change for demonstration purposes
    setTimeout(() => {
      this.isLoading$.next(true); // Set loading state to true
      setTimeout(() => {
        this.isLoading$.next(false); // Set loading state to false
      }, 3000); // 3 seconds later
    }, 1000); // 1 second after component initialization


  }
}


// function save() {
//   throw new Error('Function not implemented.');
// }

