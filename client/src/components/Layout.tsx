import React from 'react';
import Sidebar from './Sidebar';
import '../styles/Layout.css';
import type { User } from '../types';

type LayoutProps = {
  children: React.ReactNode;
  user?: User;
  onLogout?: () => void;
};

const Layout: React.FC<LayoutProps> = ({ children, user, onLogout }) => {
  return (
    <div className="layout">
      <Sidebar user={user} onLogout={onLogout} />
      <main className="main-content">
        {children}
      </main>
    </div>
  );
};

export default Layout;
