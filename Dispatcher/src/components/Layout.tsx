import { Gauge, Hospital, LogOut, MapPinned, Menu, RadioTower, Stethoscope, Truck } from 'lucide-react';
import { NavLink, Outlet } from 'react-router-dom';
import type { User } from 'firebase/auth';

const navItems = [
  { to: '/dashboard', label: 'Dashboard', icon: Gauge },
  { to: '/requests', label: 'Emergency Requests', icon: Stethoscope },
  { to: '/map', label: 'Ambulance Tracker', icon: MapPinned },
  { to: '/fleet', label: 'Fleet & Crew', icon: Truck },
  { to: '/hubs', label: 'Hubs', icon: RadioTower },
  { to: '/hospitals', label: 'Hospitals', icon: Hospital },
  { to: '/settings', label: 'Settings', icon: Menu },
];

export function Layout({ onLogout, user }: { onLogout: () => void; user: User }) {
  return <div className="app-shell"><aside className="sidebar"><div className="brand-block"><div className="brand-mark">RC</div><div><p className="eyebrow">Emergency command</p><h2>RAAH Connect</h2></div></div><nav className="nav-menu" aria-label="Sidebar navigation">{navItems.map(({ to, label, icon: Icon }) => <NavLink key={to} to={to} className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}><Icon size={18} /><span>{label}</span></NavLink>)}</nav><button type="button" className="logout-button" onClick={onLogout}><LogOut size={18} />Logout</button></aside><div className="main-panel"><header className="topbar"><div><p className="eyebrow">Operations center</p><h1>Emergency dispatcher dashboard</h1></div><div className="topbar-actions"><div className="user-pill"><div className="avatar">D</div><div><strong>{user.email}</strong><small>Dispatcher</small></div></div></div></header><main className="content-body"><Outlet /></main></div></div>;
}
