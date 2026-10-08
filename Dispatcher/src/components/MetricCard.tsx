import type { ReactNode } from 'react';

type MetricCardProps = {
  label: string;
  value: string | number;
  detail: string;
  icon: ReactNode;
  tone?: 'default' | 'red' | 'blue' | 'green' | 'amber';
};

export function MetricCard({ label, value, detail, icon, tone = 'default' }: MetricCardProps) {
  return (
    <div className={`metric-card metric-${tone}`}>
      <div className="metric-icon">{icon}</div>
      <div>
        <p className="metric-label">{label}</p>
        <h3>{value}</h3>
        <span>{detail}</span>
      </div>
    </div>
  );
}
