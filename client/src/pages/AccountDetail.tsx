import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import type { User } from '../types';
import '../styles/AccountDetail.css';

interface AccountDetailProps {
  user: User;
  onLogout: () => void;
}

type AccountType = 'DEPOSIT' | 'SAVING' | 'LOAN';
type AccountStatus = 'ACTIVE' | 'INACTIVE' | 'CLOSED';

interface AccountDetailInfo {
  id: number;
  accountNumber: string;
  type: AccountType;
  status: AccountStatus;
  openedAt: string;
}

interface ApiResponse<T> {
  result: string;
  data: T;
  error: any;
}

function RollingDigit({ targetDigit, delay }: { targetDigit: number; delay: number }) {
  const [isAnimating, setIsAnimating] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsAnimating(true);
    }, delay);

    return () => clearTimeout(timer);
  }, [delay]);

  return (
    <span className="rolling-digit-wrapper">
      <span
        className={`rolling-digit ${isAnimating ? 'animate' : ''}`}
        style={{
          '--target-digit': targetDigit,
          '--delay': `${delay}ms`,
        } as React.CSSProperties}
      >
        {[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9].map((num, idx) => (
          <span key={idx} className="digit">
            {num}
          </span>
        ))}
      </span>
    </span>
  );
}

function RollingNumber({ value }: { value: string }) {
  const targetValue = parseInt(value.replace(/,/g, ''));
  const formattedValue = targetValue.toLocaleString('ko-KR');
  const digits = formattedValue.split('');

  return (
    <span className="rolling-number">
      {digits.map((char, index) => {
        if (char === ',') {
          return <span key={`comma-${index}`} className="comma">,</span>;
        }
        const digit = parseInt(char);
        const delay = index * 30;
        return <RollingDigit key={index} targetDigit={digit} delay={delay} />;
      })}
    </span>
  );
}

function AccountDetail({ user, onLogout }: AccountDetailProps) {
  const { accountId } = useParams<{ accountId: string }>();
  const navigate = useNavigate();
  const [account, setAccount] = useState<AccountDetailInfo | null>(null);
  const [balance, setBalance] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [balanceLoading, setBalanceLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showCopyToast, setShowCopyToast] = useState(false);

  useEffect(() => {
    const fetchAccountDetail = async () => {
      if (!accountId) return;

      try {
        setLoading(true);
        const response = await fetch(
          `/api/accounts/${accountId}?userId=${user.id}`
        );

        if (!response.ok) {
          throw new Error(`Failed to fetch account: ${response.status}`);
        }

        const data: ApiResponse<AccountDetailInfo> = await response.json();

        if (data.result === 'SUCCESS' && data.data) {
          setAccount(data.data);
        }
      } catch (err) {
        console.error('Fetch account detail error:', err);
        setError(err instanceof Error ? err.message : 'Failed to load account');
      } finally {
        setLoading(false);
      }
    };

    fetchAccountDetail();
  }, [accountId, user.id]);

  const getAccountTypeName = (type: AccountType): string => {
    const typeNames = {
      DEPOSIT: '입출금',
      SAVING: '적금',
      LOAN: '대출',
    };
    return typeNames[type] || type;
  };

  const getAccountStatusName = (status: AccountStatus): string => {
    const statusNames = {
      ACTIVE: '정상',
      INACTIVE: '비활성',
      CLOSED: '해지',
    };
    return statusNames[status] || status;
  };

  const formatAccountNumber = (accountNumber: string): string => {
    const cleanNumber = accountNumber.replace(/-/g, '');

    if (cleanNumber.length < 8) return cleanNumber;

    const part1 = cleanNumber.slice(0, 4);
    const part2 = cleanNumber.slice(4, 8);
    const remaining = cleanNumber.slice(8);

    return `${part1}-${part2}-${remaining}`;
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  };

  const fetchBalance = async () => {
    if (!accountId) return;

    try {
      setBalanceLoading(true);
      const response = await fetch(
        `/api/accounts/${accountId}/balance?userId=${user.id}`
      );

      if (!response.ok) {
        throw new Error(`Failed to fetch balance: ${response.status}`);
      }

      const data: ApiResponse<{ accountId: number; balance: number }> = await response.json();

      if (data.result === 'SUCCESS' && data.data) {
        setBalance(data.data.balance.toLocaleString('ko-KR'));
      }
    } catch (err) {
      console.error('Fetch balance error:', err);
      setError(err instanceof Error ? err.message : 'Failed to load balance');
    } finally {
      setBalanceLoading(false);
    }
  };

  const copyAccountNumber = async () => {
    if (!account) return;

    try {
      await navigator.clipboard.writeText(account.accountNumber);
      setShowCopyToast(true);
      setTimeout(() => {
        setShowCopyToast(false);
      }, 2000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  };

  return (
    <Layout user={user} onLogout={onLogout}>
      <div className="account-detail-container">
        <header className="account-detail-header">
          <div className="header-row">
            <button onClick={() => navigate('/accounts')} className="back-button">
              &lt;
            </button>
            <h1>계좌 상세</h1>
          </div>
        </header>

        {loading && <p className="loading">계좌 정보를 불러오는 중...</p>}

        {error && <p className="error-message">{error}</p>}

        {!loading && account && (
          <div className="account-detail-card">
            <div className="detail-section">
              <h2 className="account-title" onClick={copyAccountNumber}>
                큐큐뱅크 {formatAccountNumber(account.accountNumber)}
              </h2>

              {balance ? (
                <div className="balance-display">
                  <span className="balance-amount">
                    <RollingNumber value={balance} />원
                  </span>
                </div>
              ) : (
                <button
                  className="balance-button"
                  onClick={fetchBalance}
                  disabled={balanceLoading}
                >
                  {balanceLoading ? '조회 중...' : '잔액 보기'}
                </button>
              )}
            </div>
          </div>
        )}

        {showCopyToast && (
          <div className="copy-toast">
            계좌번호를 복사했어요.
          </div>
        )}
      </div>
    </Layout>
  );
}

export default AccountDetail;
