# Table widget

Table widget is used to display data in table with pagination.

Inputs:

```ts
  @Input() title: string;
  @Input() data: Attack[];
```

Data input is array of `Attack` objects. `Attack` model is defined in `attack.model.ts` component as follows:
```ts
export class Attack {
    timestamp: string;
    source: string;
    destination: string;
    flows: number;
    duration: string;
}
```
