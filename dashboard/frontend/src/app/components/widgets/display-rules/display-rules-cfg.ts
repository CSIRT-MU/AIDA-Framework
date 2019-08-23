export interface ColumnHeader {
  property: string;
  label: string;
}
export const columnMapping = [{property: 'id',
                               label: 'ID'},
                              {property: 'rule',
                               label: 'Rule'},
                              {property: 'support',
                               label: 'Support'},
                              {property: 'number_of_sequences',
                               label: '# of sequences'},
                              {property: 'database',
                               label: 'Database'},
                              {property: 'algorithm',
                               label: 'Algorithm'},
                              {property: 'confidence',
                               label: 'Confidence'},
                              {property: 'inserted',
                               label: 'Insertion date'},
                              {property: 'active',
                               label: 'Active?'}];
export const maxPages = 10;
export class RuleModel {
  comment: string;
  algorithm: string;
  confidence: string;
  support: string;
  database: string;
  rule: string;
  number_of_sequences: string;
  active: number|string;
  inserted: string;
  id: string;
}
