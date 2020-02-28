import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferFeedbackComponent } from './offer-feedback.component';

describe('OfferFeedbackComponent', () => {
  let component: OfferFeedbackComponent;
  let fixture: ComponentFixture<OfferFeedbackComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OfferFeedbackComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OfferFeedbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
