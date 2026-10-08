import React from 'react';
import { Ambulance, Phone, Search, UserRound } from 'lucide-react';
import { StatusBadge } from '../components/StatusBadge';
import type { DispatcherContext } from '../App';

export function FleetPage({ data }: { data: DispatcherContext }) {
  const [search, setSearch] = React.useState('');
  const query = search.trim().toLowerCase();
  const rows = data.ambulances.map((ambulance) => {
    const driver = data.drivers.find((item) => item.id === ambulance.driverId || item.assignedAmbulanceId === ambulance.id);
    return { ambulance, driver };
  }).filter(({ ambulance, driver }) => {
    if (!query) return true;
    return [ambulance.id, ambulance.registration, ambulance.type, ambulance.status, driver?.name, driver?.phone].some((value) => String(value ?? '').toLowerCase().includes(query));
  });

  return <section className="panel">
    <div className="panel-header">
      <div><p className="eyebrow">Live fleet</p><h3>Ambulances & drivers</h3><p className="section-subtitle">One operational view for vehicle, crew and readiness.</p></div>
      <div className="search-box"><Search size={16} /><input value={search} onChange={(event) => setSearch(event.target.value)} type="search" placeholder="Search fleet or driver" aria-label="Search fleet or driver" /></div>
    </div>
    <div className="table-wrap"><table><thead><tr><th>Ambulance</th><th>Type</th><th>Driver</th><th>Contact</th><th>Status</th><th>Location</th><th>ETA</th></tr></thead><tbody>
      {rows.map(({ ambulance, driver }) => <tr key={ambulance.id}>
        <td><div className="table-name-wrap"><Ambulance size={16} /><div><strong>{ambulance.id}</strong><small>{ambulance.registration || 'Registration not set'}</small></div></div></td>
        <td>{ambulance.type}</td>
        <td><div className="table-name-wrap"><UserRound size={15} /><span>{driver?.name || 'Unassigned'}</span></div></td>
        <td>{driver?.phone ? <span className="inline-meta"><Phone size={13} />{driver.phone}</span> : '—'}</td>
        <td><StatusBadge status={ambulance.status} /></td>
        <td>{ambulance.currentLocation || (ambulance.latitude ? `${ambulance.latitude}, ${ambulance.longitude}` : '—')}</td>
        <td>{ambulance.etaMinutes ? `${ambulance.etaMinutes} min` : '—'}</td>
      </tr>)}
    </tbody></table></div>
    {!rows.length && <div className="placeholder-box"><p>No fleet records match the current search.</p></div>}
  </section>;
}

