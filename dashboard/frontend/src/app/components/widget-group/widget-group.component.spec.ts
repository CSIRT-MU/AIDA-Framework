import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WidgetGroupComponent } from './widget-group.component';

describe('WidgetGroup', () => {
  let component: WidgetGroupComponent;
  let fixture: ComponentFixture<WidgetGroupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WidgetGroupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WidgetGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
