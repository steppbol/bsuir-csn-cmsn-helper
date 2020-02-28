import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferEditComponent } from './offer-edit.component';

describe('OfferEditComponent', () => {
  let component: OfferEditComponent;
  let fixture: ComponentFixture<OfferEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OfferEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OfferEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
