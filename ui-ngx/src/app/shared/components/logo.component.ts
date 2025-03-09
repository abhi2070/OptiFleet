

import { Component } from '@angular/core';

@Component({
  selector: 'tb-logo',
  templateUrl: './logo.component.html',
  styleUrls: ['./logo.component.scss']
})
export class LogoComponent {

  logo = 'assets/synth-iot-logo.svg';

  gotooptifleet(): void {
    window.open('https://optifleet.com', '_blank');
  }

}
