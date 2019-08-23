import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthSectionComponent } from './auth-section.component';

describe('AuthSectionComponent', () => {
  let component: AuthSectionComponent;
  let fixture: ComponentFixture<AuthSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AuthSectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
