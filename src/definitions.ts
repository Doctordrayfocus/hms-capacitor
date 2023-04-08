export interface HmsCapacitorPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
