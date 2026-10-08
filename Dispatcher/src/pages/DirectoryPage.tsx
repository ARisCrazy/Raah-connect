import { useMemo, useState } from 'react';
import { Hospital as HospitalIcon, MapPin, Search, ShieldCheck, Siren, Star, Database } from 'lucide-react';
import { StatusBadge } from '../components/StatusBadge';
import { seedPrototypeHospitals } from '../services/firebaseApi';
import type { DispatcherContext } from '../App';

export function DirectoryPage({ data }: { kind: 'hospitals'; data: DispatcherContext }) {
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<'ALL' | 'ACCEPTING' | 'LIMITED' | 'NOT_ACCEPTING'>('ALL');
  const [seeding, setSeeding] = useState(false);
  const [message, setMessage] = useState('');
  const query = search.trim().toLowerCase();

  const counts = useMemo(() => ({
    total: data.hospitals.length,
    accepting: data.hospitals.filter((h) => h.emergencyAvailable && h.availableBeds > 0).length,
    limited: data.hospitals.filter((h) => h.emergencyAvailable && h.availableBeds > 0 && h.availableBeds <= 5).length,
    notAccepting: data.hospitals.filter((h) => !h.emergencyAvailable || h.availableBeds <= 0).length,
  }), [data.hospitals]);

  const hospitals = data.hospitals.filter((hospital) => {
    const matchesSearch = [hospital.name, hospital.address, hospital.type].some((value) => value.toLowerCase().includes(query));
    const status = hospital.emergencyAvailable ? (hospital.availableBeds <= 5 ? 'LIMITED' : 'ACCEPTING') : 'NOT_ACCEPTING';
    return matchesSearch && (filter === 'ALL' || status === filter);
  });

  const handleSeed = async () => {
    setSeeding(true);
    setMessage('');
    try {
      await seedPrototypeHospitals();
      setMessage('Prototype hospital records created in Firestore.');
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to create hospital records. Check Firestore permissions.');
    } finally {
      setSeeding(false);
    }
  };

  return <section className="panel hospital-page">
    <div className="panel-header">
      <div><p className="eyebrow">Hospital coordination</p><h3>Emergency hospital capacity</h3><p className="section-subtitle">Operational view for emergency acceptance, capability and available capacity.</p></div>
      <div className="hospital-actions">
        <div className="search-box"><Search size={16} /><input type="search" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search hospitals" aria-label="Search hospitals" /></div>
        <button type="button" className="secondary-button" onClick={handleSeed} disabled={seeding}><Database size={16} />{seeding ? 'Creating…' : 'Create prototype hospitals'}</button>
      </div>
    </div>

    <div className="hospital-summary-grid">
      <button className={filter === 'ALL' ? 'hospital-summary active' : 'hospital-summary'} onClick={() => setFilter('ALL')}><span>Total hospitals</span><strong>{counts.total}</strong></button>
      <button className={filter === 'ACCEPTING' ? 'hospital-summary active success' : 'hospital-summary success'} onClick={() => setFilter('ACCEPTING')}><span>Accepting</span><strong>{counts.accepting}</strong></button>
      <button className={filter === 'LIMITED' ? 'hospital-summary active warning' : 'hospital-summary warning'} onClick={() => setFilter('LIMITED')}><span>Limited capacity</span><strong>{counts.limited}</strong></button>
      <button className={filter === 'NOT_ACCEPTING' ? 'hospital-summary active danger' : 'hospital-summary danger'} onClick={() => setFilter('NOT_ACCEPTING')}><span>Not accepting</span><strong>{counts.notAccepting}</strong></button>
    </div>

    {message && <div className="inline-message">{message}</div>}

    <div className="table-wrap"><table><thead><tr><th>Hospital</th><th>Emergency</th><th>Capabilities</th><th>Capacity</th><th>Rating</th><th>Location</th><th>ETA</th></tr></thead><tbody>
      {hospitals.map((hospital) => {
        const limited = hospital.emergencyAvailable && hospital.availableBeds <= 5;
        const status = !hospital.emergencyAvailable ? 'NOT ACCEPTING' : limited ? 'LIMITED' : 'ACCEPTING';
        const capabilities = [hospital.cardiology && 'Cardiac', hospital.traumaCenter && 'Trauma', hospital.icu && 'ICU', hospital.bloodBank && 'Blood bank'].filter(Boolean).join(' · ');
        return <tr key={hospital.id}>
          <td><div className="table-name-wrap"><HospitalIcon size={17} /><div><strong>{hospital.name}</strong><small>{hospital.type} · {hospital.id}</small></div></div></td>
          <td><StatusBadge status={status} /></td>
          <td>{capabilities || 'Emergency care'}</td>
          <td><strong>{hospital.availableBeds}</strong><small>{hospital.availableBeds === 1 ? 'bed available' : 'beds available'}</small></td>
          <td>{hospital.rating > 0 ? <span className="inline-meta"><Star size={13} />{hospital.rating.toFixed(1)}</span> : '—'}</td>
          <td><span className="inline-meta"><MapPin size={13} />{hospital.address || '—'}</span></td>
          <td>{hospital.etaMinutes ? `${hospital.etaMinutes} min` : '—'}</td>
        </tr>;
      })}
    </tbody></table>{!hospitals.length && <div className="placeholder-box"><p>No hospital records are currently stored in Firebase.</p><p className="section-subtitle">Use <strong>Create prototype hospitals</strong> to initialize the collection for the RAAH demo.</p></div>}</div>

    <div className="hospital-note"><ShieldCheck size={17} /><span>Hospital records are treated as operational data. Routing distance/ETA will be calculated later when the map/routing layer is integrated.</span><Siren size={17} /><span>Prototype records are clearly intended for demonstration and testing, not real hospital capacity reporting.</span></div>
  </section>;
}
