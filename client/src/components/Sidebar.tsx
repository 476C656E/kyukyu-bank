import React from 'react';
import { NavLink, Link } from 'react-router-dom';
import '../styles/Sidebar.css';

const Sidebar = () => {
  const getNavLinkClass = ({ isActive }: { isActive: boolean }) =>
    `sidebar__button ${isActive ? 'active' : ''}`;

  return (
    <div className="sidebar">
      <ul className="sidebar__nav">
        <li className="sidebar__item">
          <NavLink to="/bank" className={getNavLinkClass}>
            큐큐 은행
          </NavLink>
        </li>
        <li className="sidebar__item">
          <NavLink to="/oepn-account" className={getNavLinkClass}>
            계좌 개설
          </NavLink>
        </li>
        <li className="sidebar__item">
          <NavLink to="/account" className={getNavLinkClass}>
            통장
          </NavLink>
        </li>
          <li className="sidebar__item">
              <NavLink to="/transaction" className={getNavLinkClass}>
                  거래내역
              </NavLink>
          </li>
          <li className="sidebar__item">
              <NavLink to="/deposit" className={getNavLinkClass}>
                  송금
              </NavLink>
          </li>
      </ul>
    </div>
  );
};

export default Sidebar;

