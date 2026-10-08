import { useState } from 'react';
import { Activity, LockKeyhole, ShieldCheck } from 'lucide-react';
import { signInWithEmailAndPassword } from 'firebase/auth';
import { auth } from '../firebase';

type LoginPageProps = { error?: string };

export function LoginPage({ error }: LoginPageProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [busy, setBusy] = useState(false);
  const [loginError, setLoginError] = useState('');

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setBusy(true);
    setLoginError('');
    try {
      await signInWithEmailAndPassword(auth, email.trim(), password);
    } catch (e) {
      setLoginError(e instanceof Error ? e.message : 'Login failed.');
    } finally {
      setBusy(false);
    }
  };

  return <div className="login-page"><div className="login-panel"><div className="login-brand"><div className="brand-mark large">RC</div><div><p className="eyebrow">Emergency coordination</p><h1>RAAH Connect</h1></div></div><div className="login-copy"><ShieldCheck size={18} /><span>Secure dispatcher access</span></div><form onSubmit={handleSubmit} className="login-form"><label>Email<input required type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="dispatcher@example.com" /></label><label>Password<input required type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" /></label>{(error || loginError) && <p className="error-text">{error || loginError}</p>}<button disabled={busy} type="submit" className="primary-button"><LockKeyhole size={16} />{busy ? 'Signing in…' : 'Login'}</button></form></div><div className="login-hero"><div className="hero-card"><div className="pulse-wrap"><Activity size={22} /></div><p className="eyebrow">Live emergency operations</p><h2>Ambulance dispatch, hospital coordination, and real-time emergency monitoring.</h2></div></div></div>;
}
