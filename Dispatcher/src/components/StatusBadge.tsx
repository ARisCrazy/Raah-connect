type StatusBadgeProps = {
  status: string;
};

const toneMap: Record<string, string> = {
  Critical: 'badge badge-critical',
  High: 'badge badge-high',
  Moderate: 'badge badge-moderate',
  Low: 'badge badge-low',
  Available: 'badge badge-success',
  AVAILABLE: 'badge badge-success',
  Assigned: 'badge badge-info',
  BUSY: 'badge badge-warning',
  'En Route to Patient': 'badge badge-warning',
  'Patient Picked Up': 'badge badge-primary',
  'En Route to Hospital': 'badge badge-primary',
  'Arrived at Hospital': 'badge badge-success',
  Completed: 'badge badge-success',
  COMPLETED: 'badge badge-success',
  Offline: 'badge badge-muted',
  OFFLINE: 'badge badge-muted',
  Maintenance: 'badge badge-muted',
  'Emergency Available': 'badge badge-success',
  'Limited Capacity': 'badge badge-warning',
  Full: 'badge badge-danger',
  'Open': 'badge badge-success',
  'Temporary Closure': 'badge badge-muted',
  New: 'badge badge-info',
  Dispatched: 'badge badge-warning',
  'Picked Up': 'badge badge-primary',
  'At Hospital': 'badge badge-success',
};

export function StatusBadge({ status }: StatusBadgeProps) {
  return <span className={toneMap[status] ?? 'badge badge-info'}>{status}</span>;
}
