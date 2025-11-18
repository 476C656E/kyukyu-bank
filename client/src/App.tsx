import { useState, useEffect } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Bank from './pages/Bank.tsx';
import Account from './pages/Account.tsx';
import AccountDetail from './pages/AccountDetail.tsx';
import Login from './pages/Login';
import OpenAccount from './pages/OpenAccount';
import './styles/App.css';
import type { User } from './types';

function App() {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setIsLoading(false);
  }, []);

  const handleLogin = (loggedInUser: User) => {
    localStorage.setItem('user', JSON.stringify(loggedInUser));
    setUser(loggedInUser);
    navigate('/banks');
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
  };

  if (isLoading) {
    return null; // 또는 로딩 스피너
  }

  return (
    <Routes>
      <Route path="/login" element={!user ? <Login onLogin={handleLogin} /> : <Navigate to="/banks" />} />
      <Route path="/banks" element={user ? <Bank user={user} onLogout={handleLogout} /> : <Navigate to="/login" />} />
      <Route path="/accounts" element={user ? <Account user={user} onLogout={handleLogout} /> : <Navigate to="/login" />} />
      <Route path="/accounts/:accountId" element={user ? <AccountDetail user={user} onLogout={handleLogout} /> : <Navigate to="/login" />} />
      <Route path="/open-accounts" element={user ? <OpenAccount user={user} onLogout={handleLogout} /> : <Navigate to="/login" />} />
      <Route path="/" element={<Navigate to={user ? "/banks" : "/login"} />} />
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}

export default App;