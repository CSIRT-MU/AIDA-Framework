import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewTableComponent } from './new-table.component';

describe('RtirtTableComponent', () => {
  let component: NewTableComponent;
  let fixture: ComponentFixture<NewTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
