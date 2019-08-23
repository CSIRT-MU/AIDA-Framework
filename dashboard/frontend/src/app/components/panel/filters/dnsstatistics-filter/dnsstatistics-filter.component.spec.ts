import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DnsstatisticsFilterComponent } from './dnsstatistics-filter.component';

describe('DnsstatisticsFilterComponent', () => {
  let component: DnsstatisticsFilterComponent;
  let fixture: ComponentFixture<DnsstatisticsFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DnsstatisticsFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DnsstatisticsFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
