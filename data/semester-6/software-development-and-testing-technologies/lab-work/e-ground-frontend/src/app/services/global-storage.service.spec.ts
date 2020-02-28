import { TestBed } from '@angular/core/testing';

import { GlobalStorageService } from './global-storage.service';

describe('GlobalStorageService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: GlobalStorageService = TestBed.get(GlobalStorageService);
    expect(service).toBeTruthy();
  });
});
