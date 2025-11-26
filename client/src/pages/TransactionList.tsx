import React, { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import type { User } from '../types';
import '../styles/TransactionList.css';

interface TransactionListProps {
  user: User;
  onLogout: () => void;
}

interface LedgerInfo {
  id: number;
  accountId: number;
  entryType: 'DEBIT' | 'CREDIT';
  amount: number;
  balanceAfter: number;
  memo: string;
  transactionDate: string;
}

interface PagingResponse<T> {
  content: T[];
  last: boolean;
  totalPages: number;
}

interface ApiResponse<T> {
  result: string;
  data: T;
  error: any;
}

function TransactionList({ user, onLogout }: TransactionListProps) {
  const [transactions, setTransactions] = useState<LedgerInfo[]>([]);
  const [page, setPage] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // 임시: 사용자 ID를 계좌 ID로 사용 (실제 서비스에선 계좌 선택 필요)
  const accountId = user.id; 

  const fetchTransactions = async (pageNum: number) => {
    try {
      setLoading(true);
      const response = await fetch(`/api/v1/ledgers?accountId=${accountId}&page=${pageNum}&size=20`);
      
      if (!response.ok) {
        throw new Error('거래내역을 불러오는데 실패했습니다.');
      }

      const json: ApiResponse<PagingResponse<LedgerInfo>> = await response.json();

      if (json.result === 'SUCCESS') {
        if (pageNum === 0) {
          setTransactions(json.data.content);
        } else {
          setTransactions(prev => [...prev, ...json.data.content]);
        }
        setHasNext(!json.data.last);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '알 수 없는 오류');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions(0);
  }, [accountId]);

  const handleLoadMore = () => {
    const nextPage = page + 1;
    setPage(nextPage);
    fetchTransactions(nextPage);
  };

  const formatAmount = (amount: number) => amount.toLocaleString('ko-KR');
  const formatDate = (dateString: string) => new Date(dateString).toLocaleString('ko-KR');

  return (
    <Layout user={user} onLogout={onLogout}>
      <div className="transaction-container">
        <header className="page-header">
          <h1>거래내역</h1>
          <p>최근 거래 내역을 확인하세요.</p>
        </header>

        {error && <div className="error-message">{error}</div>}

        <div className="transaction-list">
          {transactions.map((tx) => (
            <div key={tx.id} className="transaction-item">
              <div className="tx-left">
                <div className="tx-date">{formatDate(tx.transactionDate)}</div>
                <div className="tx-memo">{tx.memo || '내용 없음'}</div>
              </div>
              <div className="tx-right">
                <div className={`tx-amount ${tx.entryType === 'DEBIT' ? 'debit' : 'credit'}`}>
                  {tx.entryType === 'DEBIT' ? '-' : '+'}{formatAmount(tx.amount)}원
                </div>
                <div className="tx-balance">잔액 {formatAmount(tx.balanceAfter)}원</div>
              </div>
            </div>
          ))}
          
          {transactions.length === 0 && !loading && (
            <div className="empty-state">거래 내역이 없습니다.</div>
          )}
        </div>

        {hasNext && (
          <button className="load-more-btn" onClick={handleLoadMore} disabled={loading}>
            {loading ? '불러오는 중...' : '더 보기'}
          </button>
        )}
      </div>
    </Layout>
  );
}

export default TransactionList;
