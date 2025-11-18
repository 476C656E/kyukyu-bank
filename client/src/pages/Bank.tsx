import React, { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import type { User } from '../types';
import '../styles/Bank.css';

interface BankProps {
  user: User;
  onLogout: () => void;
}

interface UserInfo {
  id: number;
  name: string;
}

function Bank({ user, onLogout }: BankProps) {
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await fetch(`/api/users/${user.id}`);

        if (!response.ok) {
          const errorText = await response.text();
          console.error('API Error:', errorText);
          throw new Error(`Failed to fetch user data: ${response.status}`);
        }

        const data = await response.json();

        if (data.result) {
          setUserInfo({
            id: data.result.id,
            name: data.result.name,
          });
        }
      } catch (err) {
        console.error('Fetch error:', err);
        setError(err instanceof Error ? err.message : 'An unknown error occurred.');
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [user.id]);

  return (
    <Layout user={userInfo || user} onLogout={onLogout}>
      <div className="bank-container">
        <header className="bank-header">
          <h1>안녕하세요</h1>
        </header>

        {loading && <p>Loading...</p>}
        {error && <p className="error-message">{error}</p>}
      </div>
    </Layout>
  );
}

export default Bank;
