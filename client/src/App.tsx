import { useState, useEffect } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Bank from './pages/Bank.tsx';
import Login from './pages/Login';
import './styles/App.css';
import type { User } from './types';

function App() {
  const [user, setUser] = useState<User | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const handleLogin = (loggedInUser: User) => {
    localStorage.setItem('user', JSON.stringify(loggedInUser));
    setUser(loggedInUser);
    navigate('/bank');
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <Routes>
      <Route path="/login" element={!user ? <Login onLogin={handleLogin} /> : <Navigate to="/bank" />} />
      <Route path="/bank" element={user ? <Bank user={user} onLogout={handleLogout} /> : <Navigate to="/login" />} />
      <Route path="/" element={<Navigate to={user ? "/bank" : "/login"} />} />
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}

export default App;