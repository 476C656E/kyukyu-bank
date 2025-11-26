import React, { useState } from 'react';
import Layout from '../components/Layout';
import type { User } from '../types';
import '../styles/MockManager.css';

interface MockManagerProps {
  user: User;
  onLogout: () => void;
}

interface ApiResponse {
  result: string;
  data: string | null;
  error: { message: string } | null;
}

function MockManager({ user, onLogout }: MockManagerProps) {
  const [logs, setLogs] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const addLog = (message: string) => {
    const timestamp = new Date().toLocaleTimeString();
    setLogs(prev => [`[${timestamp}] ${message}`, ...prev]);
  };

  const handleGenerate = async (count: number, method: 'JPA' | 'TSV') => {
    if (isLoading) return;
    
    setIsLoading(true);
    addLog(`${method} 방식으로 ${count}건 데이터 생성을 요청합니다...`);

    try {
      const response = await fetch('/api/v1/internal/mock-data', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          totalCount: count,
          method: method
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result: ApiResponse = await response.json();

      if (result.result === 'SUCCESS') {
        addLog(`✅ 성공: ${result.data}`);
      } else {
        addLog(`❌ 실패: ${result.error?.message}`);
      }
    } catch (error) {
      addLog(`❌ 통신 오류: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout user={user} onLogout={onLogout}>
      <div className="mock-container">
        <header className="mock-header">
          <h1>Mock 데이터 관리자</h1>
          <p>대량의 테스트 데이터를 생성하고 관리합니다.</p>
        </header>

        <div className="control-panel">
          <div className="card">
            <h3>⚡ 빠른 생성 (TSV 방식)</h3>
            <p>파일을 생성합니다. (DB 적재는 별도)</p>
            <div className="button-group">
              <button onClick={() => handleGenerate(10000, 'TSV')} disabled={isLoading}>
                1만 건
              </button>
              <button onClick={() => handleGenerate(100000, 'TSV')} disabled={isLoading}>
                10만 건
              </button>
              <button onClick={() => handleGenerate(1000000, 'TSV')} disabled={isLoading} className="danger">
                100만 건
              </button>
            </div>
          </div>

          <div className="card">
            <h3>🐢 느린 생성 (JPA 방식)</h3>
            <p>DB에 즉시 Insert 됩니다. (매우 느림)</p>
            <div className="button-group">
              <button onClick={() => handleGenerate(100, 'JPA')} disabled={isLoading}>
                100 건
              </button>
              <button onClick={() => handleGenerate(1000, 'JPA')} disabled={isLoading}>
                1,000 건
              </button>
            </div>
          </div>
        </div>

        <div className="log-console">
          <div className="log-header">
            <span>실시간 로그</span>
            <button className="clear-btn" onClick={() => setLogs([])}>지우기</button>
          </div>
          <div className="log-content">
            {logs.length === 0 ? (
              <div className="empty-log">로그가 없습니다.</div>
            ) : (
              logs.map((log, idx) => (
                <div key={idx} className="log-line">{log}</div>
              ))
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default MockManager;
