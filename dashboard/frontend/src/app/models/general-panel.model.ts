 import { PanelGroup } from './panel_group.model';

export class Panel {
    id: number;
    name: string;
    alias: string;
    menu_name: string;
    panel_group: number;
    include_filter: boolean;
}
