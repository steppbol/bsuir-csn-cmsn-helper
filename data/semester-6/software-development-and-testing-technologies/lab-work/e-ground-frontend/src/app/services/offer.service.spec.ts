import { TestBed } from '@angular/core/testing';

import { OfferService } from './offer.service';

describe('OfferService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: OfferService = TestBed.get(OfferService);
    expect(service).toBeTruthy();
  });
});
