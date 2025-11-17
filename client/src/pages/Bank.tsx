import React, { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import type { User } from '../types';
import '../styles/Bank.css';

interface BankProps {
  user: User;
  onLogout: () => void;
}

// Mock data for demonstration - will be replaced by API data
const transactions = [
  { id: 1, date: '2025-11-15', description: 'Salary Deposit', amount: 3000, type: 'Deposit' },
  { id: 2, date: '2025-11-15', description: 'Online Shopping', amount: -150.75, type: 'Withdrawal' },
  { id: 3, date: '2025-11-14', description: 'Grocery Store', amount: -85.20, type: 'Withdrawal' },
  { id: 4, date: '2025-11-13', description: 'ATM Withdrawal', amount: -100.00, type: 'Withdrawal' },
  { id: 5, date: '2025-11-12', description: 'Friend Transfer', amount: 50.00, type: 'Deposit' },
];

interface Account {
  accountNumber: string;
  balance: number;
}

function Bank({ user, onLogout }: BankProps) {
  const [account, setAccount] = useState<Account | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAccountData = async () => {
      try {
        const response = await fetch(`/api/users/${user.id}`);
        if (!response.ok) {
          throw new Error('Failed to fetch account data.');
        }
        const data = await response.json();
        // NOTE: The current API returns UserResponse, not account details.
        // We'll simulate account data for now.
        // In a real scenario, the API should return account info.
        setAccount({
          accountNumber: '123-456-7890', // Placeholder
          balance: 5230.50, // Placeholder
        });
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An unknown error occurred.');
      } finally {
        setLoading(false);
      }
    };

    fetchAccountData();
  }, [user.id]);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
  };

  return (
    <Layout>
      <div className="bank-container">
        <header className="bank-header">
          <h1>{user.name} 고객님 안녕하세요?</h1>
          <button onClick={onLogout} className="logout-button">
            Logout
          </button>
        </header>

        {loading && <p>Loading account details...</p>}
        {error && <p className="error-message">{error}</p>}
        
        {account && (
          <>
            <div className="card account-summary">
              <h2>Account Summary</h2>
              <div className="account-details">
                <p>Account Number: {account.accountNumber}</p>
                <p className="balance">Balance: {formatCurrency(account.balance)}</p>
              </div>
            </div>

            <div className="card quick-actions">
              <h2>Quick Actions</h2>
              <div className="action-buttons">
                <button className="action-button">Transfer</button>
                <button className="action-button">Deposit</button>
                <button className="action-button">Pay Bills</button>
              </div>
            </div>

            <div className="card transactions">
              <h2>Recent Transactions</h2>
              <table className="transactions-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Description</th>
                    <th>Amount</th>
                    <th>Type</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map((tx) => (
                    <tr key={tx.id}>
                      <td>{tx.date}</td>
                      <td>{tx.description}</td>
                      <td className={tx.amount > 0 ? 'text-deposit' : 'text-withdrawal'}>
                        {formatCurrency(tx.amount)}
                      </td>
                      <td>{tx.type}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>
    </Layout>
  )
}

export default Bank;
