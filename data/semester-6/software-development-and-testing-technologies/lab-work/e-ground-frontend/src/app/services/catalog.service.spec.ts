import { TestBed } from '@angular/core/testing';

import { CatalogService } from './catalog.service';

describe('CatalogService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CatalogService = TestBed.get(CatalogService);
    expect(service).toBeTruthy();
  });
});
