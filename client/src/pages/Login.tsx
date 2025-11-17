import React, { useState } from 'react';
import '../styles/Login.css';
import type { User } from '../types';

interface LoginProps {
  onLogin: (user: User) => void;
}

const Login: React.FC<LoginProps> = ({ onLogin }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          accountId: email,
          password: password,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        onLogin(data.result);
      } else {
        alert(data.message || 'An error occurred during login.');
      }
    } catch (error) {
      console.error('Login failed:', error);
      alert('Failed to connect to the server.');
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="header-container">
          <img src="/chihuahua.jpg" alt="kyukyu logo" className="logo-image" />
          <h1>kyukyu bank 개인</h1>
        </div>
        
        <main>
          <form onSubmit={handleLogin}>
            <div className="input-wrapper">
              <input
                className="input-field"
                placeholder="이메일"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                name="email"
                aria-label="이메일"
                required
              />
            </div>
            <div style={{ height: '16px' }}></div>
            <div className="input-wrapper">
              <input
                className="input-field"
                placeholder="비밀번호"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                name="password"
                aria-label="비밀번호"
                required
              />
            </div>
            <div style={{ height: '24px' }}></div>
            <button className="login-button" type="submit">
              로그인
            </button>
          </form>
        </main>
      </div>
    </div>
  );
};

export default Login;