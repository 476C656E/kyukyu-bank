import React from 'react';
import { NavLink, Link } from 'react-router-dom';
import '../styles/Sidebar.css';
import type { User } from '../types';

interface SidebarProps {
  user?: User;
  onLogout?: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ user, onLogout }) => {
  const getNavLinkClass = ({ isActive }: { isActive: boolean }) =>
    `sidebar__button ${isActive ? 'active' : ''}`;

  return (
    <div className="sidebar">
      <ul className="sidebar__nav">
        <li className="sidebar__item">
          <NavLink to="/banks" className={getNavLinkClass}>
            큐큐 은행
          </NavLink>
        </li>
        <li className="sidebar__item">
          <NavLink to="/open-accounts" className={getNavLinkClass}>
            계좌 개설
          </NavLink>
        </li>
        <li className="sidebar__item">
          <NavLink to="/accounts" className={getNavLinkClass}>
            통장
          </NavLink>
        </li>
          <li className="sidebar__item">
              <NavLink to="/transactions" className={getNavLinkClass}>
                  거래내역
              </NavLink>
          </li>
          <li className="sidebar__item">
              <NavLink to="/deposits" className={getNavLinkClass}>
                  송금
              </NavLink>
          </li>
          <li className="sidebar__item">
              <NavLink to="/options" className={getNavLinkClass}>
                  옵션
              </NavLink>
          </li>
          <li className="sidebar__item">
              <NavLink to="/mock" className={getNavLinkClass}>
                  Mock 관리
              </NavLink>
          </li>
      </ul>

      {(user || onLogout) && (
        <div className="sidebar__footer">
          {user && (
            <div className="sidebar__user-info">
            </div>
          )}
          {onLogout && (
            <button onClick={onLogout} className="sidebar__logout-button">
              로그아웃
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default Sidebar;

