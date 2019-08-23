import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ColumnSortComponent } from './column-sort.component';

describe('ColumnSortComponent', () => {
  let component: ColumnSortComponent;
  let fixture: ComponentFixture<ColumnSortComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ColumnSortComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ColumnSortComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
