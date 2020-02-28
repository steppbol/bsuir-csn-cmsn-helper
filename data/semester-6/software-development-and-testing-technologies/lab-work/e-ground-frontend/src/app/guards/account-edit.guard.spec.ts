import { TestBed, async, inject } from '@angular/core/testing';

import { AccountEditGuard } from './account-edit.guard';

describe('AccountEditGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AccountEditGuard]
    });
  });

  it('should ...', inject([AccountEditGuard], (guard: AccountEditGuard) => {
    expect(guard).toBeTruthy();
  }));
});
