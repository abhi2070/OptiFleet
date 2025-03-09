import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UplinkService {

  private uplinkNameSource = new BehaviorSubject<string>('');
  uplinkName$ = this.uplinkNameSource.asObservable();

  storeUplinkName(uplinkName: string): void {
    this.uplinkNameSource.next(uplinkName);
  }

}
