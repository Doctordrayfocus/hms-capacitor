import { registerPlugin } from '@capacitor/core';

import type { HmsCapacitorPlugin } from './definitions';

const HmsCapacitor = registerPlugin<HmsCapacitorPlugin>('HmsCapacitor', {
  web: () => import('./web').then(m => new m.HmsCapacitorWeb()),
});

export * from './definitions';
export { HmsCapacitor };
