import { MapPinned, RadioTower } from 'lucide-react';
import type { DispatcherContext } from '../App';

export function HubsPage({ data }: { data: DispatcherContext }) {
  return <section className="panel">
    <div className="panel-header">
      <div><p className="eyebrow">Operational bases</p><h3>Ambulance hubs</h3><p className="section-subtitle">Monitor deployment points and the fleet associated with each hub.</p></div>
      <div className="compact-count"><RadioTower size={16} />{data.hubs.length} hubs</div>
    </div>
    {data.hubs.length === 0 ? <div className="placeholder-box"><strong>No hub records yet.</strong><p>Add documents to the Firestore <code>hubs</code> collection to populate this section.</p><p className="helper-text">Current fields: name, address, latitude, longitude.</p></div> : <div className="hub-grid">
      {data.hubs.map((hub) => {
        const fleet = data.ambulances.filter((ambulance) => ambulance.hubId === hub.id || ambulance.hubId === hub.name);
        const available = fleet.filter((ambulance) => /AVAILABLE/i.test(ambulance.status)).length;
        return <article className="hub-card" key={hub.id}>
          <div className="hub-card-head"><div className="hub-icon"><MapPinned size={18} /></div><div><strong>{hub.name}</strong><small>{hub.address || 'Address not set'}</small></div></div>
          <div className="hub-stats"><div><span>Fleet</span><strong>{fleet.length}</strong></div><div><span>Available</span><strong>{available}</strong></div><div><span>Coordinates</span><strong>{hub.latitude.toFixed(4)}, {hub.longitude.toFixed(4)}</strong></div></div>
        </article>;
      })}
    </div>}
  </section>;
}
