import { TestBed, async, inject } from '@angular/core/testing';

import { OfferEditGuard } from './offer-edit.guard';

describe('OfferEditGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OfferEditGuard]
    });
  });

  it('should ...', inject([OfferEditGuard], (guard: OfferEditGuard) => {
    expect(guard).toBeTruthy();
  }));
});
