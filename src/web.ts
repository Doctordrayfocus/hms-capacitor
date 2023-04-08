import { WebPlugin } from '@capacitor/core';

import type { HmsCapacitorPlugin } from './definitions';

export class HmsCapacitorWeb extends WebPlugin implements HmsCapacitorPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
