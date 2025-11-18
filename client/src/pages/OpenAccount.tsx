import React, { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import type { User } from '../types';
import '../styles/OpenAccount.css';

interface OpenAccountProps {
  user: User;
  onLogout?: () => void;
}

type AccountType = 'DEPOSIT' | 'SAVING' | 'LOAN' | '';
type AccountCurrency = 'KRW' | 'JPY' | 'USD' | '';

interface CreateAccountRequest {
  userId: number;
  accountPassword: string;
  type: AccountType;
  currency: AccountCurrency;
}

function OpenAccount({ user, onLogout }: OpenAccountProps) {
  const [accountType, setAccountType] = useState<AccountType>('');
  const [currency, setCurrency] = useState<AccountCurrency>('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  // 필드 표시 상태
  const [showCurrency, setShowCurrency] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  // 비밀번호 불일치 상태
  const [passwordMismatch, setPasswordMismatch] = useState(false);

  // 계좌 유형 선택 시 통화 필드 표시
  useEffect(() => {
    if (accountType && accountType !== '') {
      setTimeout(() => setShowCurrency(true), 100);
    } else {
      setShowCurrency(false);
      setShowPassword(false);
      setShowConfirmPassword(false);
    }
  }, [accountType]);

  // 통화 선택 시 비밀번호 필드 표시
  useEffect(() => {
    if (currency && currency !== '') {
      setTimeout(() => setShowPassword(true), 100);
    } else {
      setShowPassword(false);
      setShowConfirmPassword(false);
    }
  }, [currency]);

  // 비밀번호 입력 시 확인 필드 표시
  useEffect(() => {
    if (password && password.length > 0) {
      setTimeout(() => setShowConfirmPassword(true), 100);
    } else {
      setShowConfirmPassword(false);
    }
  }, [password]);

  // 비밀번호 일치 여부 확인
  useEffect(() => {
    if (confirmPassword.length > 0 && password !== confirmPassword) {
      setPasswordMismatch(true);
    } else {
      setPasswordMismatch(false);
    }
  }, [password, confirmPassword]);

  // 폼 유효성 검사
  const isFormValid =
    accountType !== '' &&
    currency !== '' &&
    password.length >= 4 &&
    confirmPassword.length >= 4 &&
    !passwordMismatch;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    // 비밀번호 검증
    if (password !== confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (password.length < 4) {
      setError('비밀번호는 최소 4자리 이상이어야 합니다.');
      return;
    }

    setLoading(true);

    try {
      const requestData: CreateAccountRequest = {
        userId: user.id,
        accountPassword: password,
        type: accountType,
        currency: currency,
      };

      const response = await fetch('/api/accounts', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData),
      });

      if (!response.ok) {
        throw new Error('계좌 개설에 실패했습니다.');
      }

      const data = await response.json();
      setSuccess(true);

      // 폼 초기화
      setPassword('');
      setConfirmPassword('');
      setAccountType('');
      setCurrency('');
    } catch (err) {
      setError(err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout user={user} onLogout={onLogout}>
      <div className="open-account-container">
        <header className="page-header">
          <h1>계좌 개설</h1>
          <p>빠르고 간단하게 계좌를 만들 수 있어요.</p>
        </header>

        <div className="card form-card">
          <form onSubmit={handleSubmit} className="account-form">
            <div className="form-group">
              <label>어떤 계좌를 만들까요?</label>
              <div className="account-type-buttons">
                <button
                  type="button"
                  className={`account-type-button ${accountType === 'DEPOSIT' ? 'active' : ''}`}
                  onClick={() => setAccountType('DEPOSIT')}
                >
                  입출금
                </button>
                <button
                  type="button"
                  className={`account-type-button ${accountType === 'SAVING' ? 'active' : ''}`}
                  onClick={() => setAccountType('SAVING')}
                >
                  적금
                </button>
                <button
                  type="button"
                  className={`account-type-button ${accountType === 'LOAN' ? 'active' : ''}`}
                  onClick={() => setAccountType('LOAN')}
                >
                  대출
                </button>
              </div>
            </div>

            {showCurrency && (
              <div className="form-group form-group-animated">
                <label>어떤 통화로 사용할까요?</label>
                <div className="currency-buttons">
                  <button
                    type="button"
                    className={`currency-button ${currency === 'KRW' ? 'active' : ''}`}
                    onClick={() => setCurrency('KRW')}
                  >
                    <span className="currency-icon">🇰🇷</span>
                    <span className="currency-name">원화</span>
                  </button>
                  <button
                    type="button"
                    className={`currency-button ${currency === 'USD' ? 'active' : ''}`}
                    onClick={() => setCurrency('USD')}
                  >
                    <span className="currency-icon">🇺🇸</span>
                    <span className="currency-name">달러</span>
                  </button>
                  <button
                    type="button"
                    className={`currency-button ${currency === 'JPY' ? 'active' : ''}`}
                    onClick={() => setCurrency('JPY')}
                  >
                    <span className="currency-icon">🇯🇵</span>
                    <span className="currency-name">엔화</span>
                  </button>
                </div>
              </div>
            )}

            {showPassword && (
              <div className="form-group form-group-animated">
                <label htmlFor="password">비밀번호를 정해주세요</label>
                <input
                  type="password"
                  id="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="form-control"
                  placeholder="4자리 이상 입력하세요"
                  required
                />
              </div>
            )}

            {showConfirmPassword && (
              <div className="form-group form-group-animated">
                <label htmlFor="confirmPassword">한 번 더 입력해주세요</label>
                <input
                  type="password"
                  id="confirmPassword"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className={`form-control ${passwordMismatch ? 'error' : ''}`}
                  placeholder="비밀번호를 다시 입력하세요"
                  required
                />
                {passwordMismatch && (
                  <span className="error-message">비밀번호가 달라요</span>
                )}
              </div>
            )}

            {error && <div className="alert alert-error">{error}</div>}
            {success && (
              <div className="alert alert-success">
                계좌가 성공적으로 개설되었습니다!
              </div>
            )}

            {isFormValid && (
              <button
                type="submit"
                className="submit-button form-group-animated"
                disabled={loading}
              >
                {loading ? '처리 중...' : '계좌 개설'}
              </button>
            )}
          </form>
        </div>

        <div className="info-section">
          <h3>📣 꼭 알아두세요</h3>
          <ul>
            <li>순식간에 계좌가 만들어져요</li>
            <li>비밀번호는 4자리 이상 숫자로 입력해주세요</li>
            <li>입출금 계좌로 자유롭게 돈을 보내고 받아요</li>
            <li>적금 계좌로 꾸준히 저축해보세요</li>
            <li>대출 계좌로 필요한 만큼 빌려 쓸 수 있어요</li>
          </ul>
        </div>
      </div>
    </Layout>
  );
}

export default OpenAccount;
