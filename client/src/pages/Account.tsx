import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import type { User } from '../types';
import '../styles/Account.css';

interface AccountProps {
  user: User;
  onLogout: () => void;
}

type AccountType = 'DEPOSIT' | 'SAVING' | 'LOAN';
type AccountStatus = 'ACTIVE' | 'INACTIVE' | 'CLOSED';

interface AccountInfo {
  id: number;
  accountNumber: string;
  type: AccountType;
  status: AccountStatus;
  openedAt: string;
}

interface PagingResponse<T> {
  content: T[];
  hasNext: boolean;
}

interface ApiResponse<T> {
  result: string;
  data: T;
  error: any;
}

function Account({ user, onLogout }: AccountProps) {
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState<AccountInfo[]>([]);
  const [hasNext, setHasNext] = useState(false);
  const [offset, setOffset] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const limit = 10;

  const fetchAccounts = async (offsetValue: number = 0) => {
    try {
      setLoading(true);
      const response = await fetch(
        `/api/accounts?userId=${user.id}&offset=${offsetValue}&limit=${limit}`
      );

      if (!response.ok) {
        throw new Error(`Failed to fetch accounts: ${response.status}`);
      }

      const data: ApiResponse<PagingResponse<AccountInfo>> = await response.json();

      if (data.result === 'SUCCESS' && data.data) {
        if (offsetValue === 0) {
          setAccounts(data.data.content);
        } else {
          setAccounts(prev => [...prev, ...data.data.content]);
        }
        setHasNext(data.data.hasNext);
      }
    } catch (err) {
      console.error('Fetch accounts error:', err);
      setError(err instanceof Error ? err.message : 'Failed to load accounts');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchAccounts(0);
    }
  }, [user.id]);

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
    // 기존 하이픈 제거
    const cleanNumber = accountNumber.replace(/-/g, '');

    if (cleanNumber.length < 8) return cleanNumber;

    // 4자리-4자리-나머지 형태로 포맷팅
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

  const handleLoadMore = () => {
    const newOffset = offset + limit;
    setOffset(newOffset);
    fetchAccounts(newOffset);
  };

  const handleAccountClick = (accountId: number) => {
    navigate(`/accounts/${accountId}`);
  };

  return (
    <Layout user={user} onLogout={onLogout}>
      <div className="account-container">
        <header className="account-header">
          <h1>내 계좌</h1>
        </header>

        {loading && offset === 0 && <p className="loading">계좌 정보를 불러오는 중...</p>}

        {error && <p className="error-message">{error}</p>}

        {!loading && accounts.length === 0 && (
          <div className="empty-state">
            <p>보유한 계좌가 없습니다.</p>
            <button onClick={() => navigate('/open-accounts')}>
              계좌 개설하기
            </button>
          </div>
        )}

        {accounts.length > 0 && (
          <>
            <div className="accounts-list">
              {accounts.map((account) => (
                <div
                  key={account.accountNumber}
                  className="account-card"
                  onClick={() => handleAccountClick(account.id)}
                >
                  <div className="account-card-header">
                    <span className="account-type">
                      {getAccountTypeName(account.type)}
                    </span>
                    <span className={`account-status status-${account.status.toLowerCase()}`}>
                      {getAccountStatusName(account.status)}
                    </span>
                  </div>

                  <div className="account-number">
                    {formatAccountNumber(account.accountNumber)}
                  </div>

                  <div className="account-footer">
                    <span className="opened-date">
                      개설일: {formatDate(account.openedAt)}
                    </span>
                  </div>
                </div>
              ))}
            </div>

            {hasNext && (
              <button
                className="load-more-btn"
                onClick={handleLoadMore}
                disabled={loading}
              >
                {loading ? '불러오는 중...' : '더 보기'}
              </button>
            )}
          </>
        )}
      </div>
    </Layout>
  );
}

export default Account;
