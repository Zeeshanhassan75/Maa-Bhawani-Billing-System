import React, { useState } from 'react';
import InvoiceForm from './InvoiceForm';
import History from './History';
import './Dashboard.css';

const Dashboard = ({ onLogout }) => {
    const [activeTab, setActiveTab] = useState('create');

    return (
        <div className="layout-container-top">
            <nav className="top-navbar">
                <div className="navbar-brand">
                    MAA BHAWANI TRADERS
                </div>
                <div className="navbar-menu">
                    <button
                        className={`nav-btn ${activeTab === 'create' ? 'active' : ''}`}
                        onClick={() => setActiveTab('create')}
                    >
                        <span>➕</span> Create New Bill
                    </button>
                    <button
                        className={`nav-btn ${activeTab === 'history' ? 'active' : ''}`}
                        onClick={() => setActiveTab('history')}
                    >
                        <span>📜</span> Bill History
                    </button>
                </div>
                <div className="navbar-actions">
                    <button className="btn-logout" onClick={onLogout}>
                        Logout
                    </button>
                </div>
            </nav>

            <main className="main-content-top">
                {activeTab === 'create' && <InvoiceForm onViewHistory={() => setActiveTab('history')} />}
                {activeTab === 'history' && <History />}
            </main>
        </div>
    );
};

export default Dashboard;
