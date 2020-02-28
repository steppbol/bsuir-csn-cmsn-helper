import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSidenavComponent } from './user-sidenav.component';

describe('UserSidenavComponent', () => {
  let component: UserSidenavComponent;
  let fixture: ComponentFixture<UserSidenavComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserSidenavComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserSidenavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
