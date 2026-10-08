import { useEffect, useState } from 'react';
import { HashRouter, Navigate, Route, Routes } from 'react-router-dom';
import { onAuthStateChanged, signOut, type User } from 'firebase/auth';
import { collection, doc, getDoc } from 'firebase/firestore';
import { Layout } from './components/Layout';
import { DashboardPage } from './pages/DashboardPage';
import { DirectoryPage } from './pages/DirectoryPage';
import { LoginPage } from './pages/LoginPage';
import { FleetPage } from './pages/FleetPage';
import { HubsPage } from './pages/HubsPage';
import { auth, db } from './firebase';
import { subscribeDispatcherData } from './services/firebaseApi';
import type { Ambulance, Driver, EmergencyCase, Hospital, Hub } from './types';

export type DispatcherContext = {
  emergencies: EmergencyCase[];
  ambulances: Ambulance[];
  hospitals: Hospital[];
  drivers: Driver[];
  hubs: Hub[];
};

export const emptyDispatcherData: DispatcherContext = {
  emergencies: [],
  ambulances: [],
  hospitals: [],
  drivers: [],
  hubs: [],
};

function RequestsPage({ data }: { data: DispatcherContext }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <div><p className="eyebrow">Emergency requests</p><h3>New and active emergency cases</h3></div>
      </div>
      {data.emergencies.length === 0 ? <div className="placeholder-box"><p>No emergency requests are currently stored in Firebase.</p></div> : (
        <div className="table-wrap"><table><thead><tr><th>Emergency</th><th>Patient</th><th>Type</th><th>Severity</th><th>Ambulance</th><th>Hospital</th><th>Status</th></tr></thead><tbody>
          {data.emergencies.map((item) => <tr key={item.id}><td><strong>{item.id}</strong><small>{item.pickupLocation}</small></td><td>{item.patientName}</td><td>{item.emergencyType}</td><td>{item.severity}</td><td>{item.ambulanceId ?? 'Unassigned'}</td><td>{item.hospitalName ?? item.hospitalId ?? 'TBD'}</td><td>{item.status}</td></tr>)}
        </tbody></table></div>
      )}
    </section>
  );
}

function NotificationsPage({ data }: { data: DispatcherContext }) {
  const items = data.emergencies.slice(0, 8);
  return <section className="panel"><div className="panel-header"><div><p className="eyebrow">Live event feed</p><h3>Notifications</h3></div></div><div className="notification-list">{items.length === 0 ? <div className="placeholder-box"><p>No live emergency events.</p></div> : items.map((item) => <div className="notification-item unread" key={item.id}><div className="dot" /><div><strong>{item.id} · {item.status}</strong><p>{item.emergencyType} for {item.patientName} at {item.pickupLocation}.</p><small>{item.requestTime || 'Recent'}</small></div></div>)}</div></section>;
}

function MapPage({ data }: { data: DispatcherContext }) {
  return <section className="panel"><div className="panel-header"><div><p className="eyebrow">Realtime Database</p><h3>Ambulance tracker</h3></div></div><div className="table-wrap"><table><thead><tr><th>Ambulance</th><th>Status</th><th>Latitude</th><th>Longitude</th><th>Speed</th><th>Updated</th></tr></thead><tbody>{data.ambulances.map((ambulance) => <tr key={ambulance.id}><td>{ambulance.id}</td><td>{ambulance.status}</td><td>{ambulance.latitude || '—'}</td><td>{ambulance.longitude || '—'}</td><td>—</td><td>Live Firebase location</td></tr>)}</tbody></table></div></section>;
}

function SettingsPage({ user }: { user: User }) {
  return <section className="panel"><div className="panel-header"><div><p className="eyebrow">Firebase Authentication</p><h3>Dispatcher account</h3></div></div><div className="placeholder-box"><p><strong>{user.email}</strong></p><p>Access is controlled through Firebase Authentication and the user's Firestore role.</p></div></section>;
}

function AppRoutes({ data, user, onLogout }: { data: DispatcherContext; user: User; onLogout: () => void }) {
  return <Routes>
    <Route path="/" element={<Layout onLogout={onLogout} user={user} />}>
      <Route index element={<Navigate to="/dashboard" replace />} />
      <Route path="dashboard" element={<DashboardPage data={data} />} />
      <Route path="operations" element={<DashboardPage data={data} />} />
      <Route path="requests" element={<RequestsPage data={data} />} />
      <Route path="map" element={<MapPage data={data} />} />
      <Route path="fleet" element={<FleetPage data={data} />} />
      <Route path="hospitals" element={<DirectoryPage kind="hospitals" data={data} />} />
      <Route path="hubs" element={<HubsPage data={data} />} />
      <Route path="notifications" element={<NotificationsPage data={data} />} />
      <Route path="settings" element={<SettingsPage user={user} />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Route>
  </Routes>;
}

function App() {
  const [user, setUser] = useState<User | null>(null);
  const [authReady, setAuthReady] = useState(false);
  const [roleAllowed, setRoleAllowed] = useState(false);
  const [data, setData] = useState<DispatcherContext>(emptyDispatcherData);
  const [error, setError] = useState('');

  useEffect(() => onAuthStateChanged(auth, async (nextUser) => {
    setUser(nextUser);
    setAuthReady(true);
    setRoleAllowed(false);
    if (!nextUser) return;
    try {
      const profile = await getDoc(doc(collection(db, 'users'), nextUser.uid));
      const role = profile.data()?.role;
      if (role !== 'DISPATCHER' && role !== 'ADMIN') {
        setError('This account is not registered as a dispatcher/admin in Firestore.');
        await signOut(auth);
        return;
      }
      setRoleAllowed(true);
      setError('');
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Unable to load dispatcher profile.');
      await signOut(auth);
    }
  }), []);

  useEffect(() => {
    if (!user || !roleAllowed) return;
    return subscribeDispatcherData(setData, (e) => setError(e.message));
  }, [user, roleAllowed]);

  if (!authReady) return <div className="login-page"><div className="login-panel"><h2>Connecting to Firebase…</h2></div></div>;
  if (error && !user) return <LoginPage error={error} />;
  if (!user || !roleAllowed) return <LoginPage error={error} />;

  return <HashRouter><AppRoutes data={data} user={user} onLogout={() => signOut(auth)} /></HashRouter>;
}

export default App;
