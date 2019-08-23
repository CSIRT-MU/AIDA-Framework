export class Filter {
  aggregation: string;
  dateFrom: Date;
  dateTo: Date;
  ip: string;

  setInterval(hours: number) {
    this.dateTo = new Date();
    this.dateFrom = new Date(this.dateTo.getTime() - hours*60*60*1000);
  }
}
