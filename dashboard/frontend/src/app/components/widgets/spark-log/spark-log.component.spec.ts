import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SparkLogComponent } from './spark-log.component';

describe('SparkLogComponent', () => {
  let component: SparkLogComponent;
  let fixture: ComponentFixture<SparkLogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SparkLogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SparkLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
