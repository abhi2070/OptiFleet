import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class LatestTimeseries {
    private latestTimeseriesSubject = new BehaviorSubject<any>(null);
    latestTimeseries$ = this.latestTimeseriesSubject.asObservable();

    updateLatestTimeseries(data: any): void {
        this.latestTimeseriesSubject.next(data);
    }

}
