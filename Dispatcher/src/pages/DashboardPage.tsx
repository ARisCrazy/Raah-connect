import { useEffect, useMemo, useState } from 'react';
import { Ambulance, CheckCircle2, Hospital, MapPinned, ShieldAlert, Siren, Wifi } from 'lucide-react';
import { StatusBadge } from '../components/StatusBadge';
import { MetricCard } from '../components/MetricCard';
import { subscribeAmbulanceLocations } from '../services/firebaseApi';
import type { DispatcherContext } from '../App';

export function DashboardPage({ data }: { data: DispatcherContext }) {
  const [locations, setLocations] = useState<Record<string, { latitude: number; longitude: number; speed?: number; timestamp?: number }>>({});

  useEffect(() => subscribeAmbulanceLocations(setLocations), []);

  const metrics = useMemo(() => ({
    activeEmergencies: data.emergencies.filter((e) => !['COMPLETED', 'Completed'].includes(e.status)).length,
    availableAmbulances: data.ambulances.filter((a) => /AVAILABLE/i.test(a.status)).length,
    enRoute: data.emergencies.filter((e) => /EN_ROUTE_TO_PATIENT|EN_ROUTE_TO_HOSPITAL|EN ROUTE/i.test(e.status)).length,
    criticalCases: data.emergencies.filter((e) => /critical/i.test(e.severity)).length,
  }), [data]);

  const rows = data.emergencies.slice(0, 8);
  const openHospitals = data.hospitals.filter((hospital) => hospital.emergencyAvailable && hospital.availableBeds > 0).length;

  return <>
    <section className="metrics-grid">
      <MetricCard label="Active emergencies" value={metrics.activeEmergencies} detail="Live requests" icon={<Siren size={18} />} tone="red" />
      <MetricCard label="Available fleet" value={metrics.availableAmbulances} detail="Ready for dispatch" icon={<Ambulance size={18} />} tone="blue" />
      <MetricCard label="En route" value={metrics.enRoute} detail="Active ambulance movement" icon={<MapPinned size={18} />} tone="amber" />
      <MetricCard label="Critical cases" value={metrics.criticalCases} detail="Priority attention" icon={<ShieldAlert size={18} />} tone="green" />
    </section>

    <section className="content-grid">
      <div className="panel span-2"><div className="panel-header"><div><p className="eyebrow">Live queue</p><h3>Emergency coordination</h3><p className="section-subtitle">Focus on requests that need dispatch attention first.</p></div><div className="live-indicator"><span className="live-dot" /><Wifi size={14} />Live</div></div><div className="table-wrap"><table><thead><tr><th>Emergency</th><th>Priority</th><th>Ambulance</th><th>Driver</th><th>Hospital</th><th>ETA</th><th>Status</th></tr></thead><tbody>{rows.map((row) => <tr key={row.id}><td><strong>{row.id}</strong><small>{row.pickupLocation}</small></td><td><StatusBadge status={row.severity} /></td><td>{row.ambulanceId ?? 'Unassigned'}</td><td>{row.driverName ?? 'Pending'}</td><td>{row.hospitalName ?? row.hospitalId ?? 'TBD'}</td><td>{row.etaMinutes ? `${row.etaMinutes} min` : '—'}</td><td><StatusBadge status={row.status} /></td></tr>)}</tbody></table>{rows.length === 0 && <div className="placeholder-box"><p>No emergency requests are currently stored in Firebase.</p></div>}</div></div>
      <div className="panel"><div className="panel-header"><div><p className="eyebrow">Control room</p><h3>Current snapshot</h3></div></div><div className="snapshot-list"><div className="snapshot-item"><div className="snapshot-icon red"><ShieldAlert size={16} /></div><div><strong>{metrics.criticalCases} critical</strong><small>Emergency cases requiring priority attention</small></div></div><div className="snapshot-item"><div className="snapshot-icon green"><CheckCircle2 size={16} /></div><div><strong>{openHospitals} hospitals open</strong><small>Accepting emergency cases with beds available</small></div></div><div className="snapshot-item"><div className="snapshot-icon blue"><Hospital size={16} /></div><div><strong>{data.hubs.length} hubs</strong><small>Operational ambulance bases configured</small></div></div></div></div>
    </section>

    <section className="content-grid">
      <div className="panel span-2"><div className="panel-header"><div><p className="eyebrow">Realtime Database</p><h3>Ambulance locations</h3></div></div><div className="table-wrap"><table><thead><tr><th>Ambulance</th><th>Status</th><th>Latitude</th><th>Longitude</th><th>Speed</th><th>Last update</th></tr></thead><tbody>{data.ambulances.map((ambulance) => { const live = locations[ambulance.id]; return <tr key={ambulance.id}><td><strong>{ambulance.id}</strong><small>{ambulance.registration || 'Registration not set'}</small></td><td><StatusBadge status={ambulance.status} /></td><td>{(live?.latitude ?? ambulance.latitude) || '—'}</td><td>{(live?.longitude ?? ambulance.longitude) || '—'}</td><td>{live?.speed != null ? `${live.speed} km/h` : '—'}</td><td>{live?.timestamp ? new Date(live.timestamp).toLocaleTimeString() : 'Waiting for GPS'}</td></tr>; })}</tbody></table></div></div>
    </section>
  </>;
}
