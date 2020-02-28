import { TestBed } from '@angular/core/testing';

import { GlobalUserStorageService } from './global-user-storage.service';

describe('GlobalUserStorageService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: GlobalUserStorageService = TestBed.get(GlobalUserStorageService);
    expect(service).toBeTruthy();
  });
});
