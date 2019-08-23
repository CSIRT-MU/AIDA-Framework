import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DefaultFilterComponent } from './default-filter.component';

describe('DefaultFilterComponent', () => {
  let component: DefaultFilterComponent;
  let fixture: ComponentFixture<DefaultFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DefaultFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DefaultFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
