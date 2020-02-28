import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogSearchToolbarComponent } from './catalog-search-toolbar.component';

describe('CatalogSearchToolbarComponent', () => {
  let component: CatalogSearchToolbarComponent;
  let fixture: ComponentFixture<CatalogSearchToolbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CatalogSearchToolbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CatalogSearchToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
