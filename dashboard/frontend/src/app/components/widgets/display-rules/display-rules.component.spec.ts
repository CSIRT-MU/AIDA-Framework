import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayRulesComponent } from './display-rules.component';

describe('DisplayRulesComponent', () => {
  let component: DisplayRulesComponent;
  let fixture: ComponentFixture<DisplayRulesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DisplayRulesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DisplayRulesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
