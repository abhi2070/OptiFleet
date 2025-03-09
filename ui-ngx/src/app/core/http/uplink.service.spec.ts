import { TestBed } from '@angular/core/testing';

import { UplinkService } from './uplink.service';

describe('UplinkService', () => {
  let service: UplinkService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UplinkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
