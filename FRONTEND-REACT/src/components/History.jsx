import React, { useState, useEffect } from 'react';
import './History.css';

const History = () => {
    const [invoices, setInvoices] = useState([]);
    const [loading, setLoading] = useState(true);

    // Filters
    const [filterName, setFilterName] = useState('');
    const [filterInvoiceNo, setFilterInvoiceNo] = useState('');
    const [filterDate, setFilterDate] = useState('');

    useEffect(() => {
        fetchInvoices();
    }, []);

    const fetchInvoices = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/invoices', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            if (res.ok) {
                const data = await res.json();
                setInvoices(data);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const downloadFile = async (id, type) => {
        try {
            const res = await fetch(`http://localhost:8080/api/invoices/${id}/${type}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            if (res.ok) {
                const blob = await res.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `invoice-${id}.${type === 'pdf' ? 'pdf' : 'xlsx'}`;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
            }
        } catch (err) {
            console.error('Error downloading file:', err);
        }
    };

    const handlePrint = (id) => {
        window.open(`http://localhost:8080/api/invoices/${id}/pdf`, '_blank');
    };

    const filteredInvoices = invoices.filter(inv => {
        const matchName = inv.consigneeName?.toLowerCase().includes(filterName.toLowerCase());
        const matchInvoiceNo = inv.invoiceNo?.toLowerCase().includes(filterInvoiceNo.toLowerCase());
        const matchDate = filterDate ? inv.date === filterDate : true;
        return matchName && matchInvoiceNo && matchDate;
    });

    return (
        <div className="history-container">
            <div className="page-header" style={{ marginBottom: '1rem' }}>
                <h1 className="page-title">Bill History</h1>
                <button onClick={fetchInvoices} className="btn-secondary">🔃 Refresh</button>
            </div>

            <div className="glass-panel filters-panel">
                <div className="form-grid-3">
                    <div className="input-group">
                        <label>Filter by Consignee</label>
                        <input type="text" placeholder="Search name..." value={filterName} onChange={e => setFilterName(e.target.value)} />
                    </div>
                    <div className="input-group">
                        <label>Filter by Invoice No</label>
                        <input type="text" placeholder="Search invoice..." value={filterInvoiceNo} onChange={e => setFilterInvoiceNo(e.target.value)} />
                    </div>
                    <div className="input-group">
                        <label>Filter by Date</label>
                        <input type="date" value={filterDate} onChange={e => setFilterDate(e.target.value)} />
                    </div>
                </div>
            </div>

            <div className="glass-panel" style={{ padding: '0', overflow: 'hidden', marginTop: '1.5rem' }}>
                {loading ? (
                    <div className="loading-state">Loading invoices...</div>
                ) : filteredInvoices.length === 0 ? (
                    <div className="empty-state">No invoices found. Create a new bill to see it here.</div>
                ) : (
                    <table className="history-table">
                        <thead>
                            <tr>
                                <th>Invoice No</th>
                                <th>Date</th>
                                <th>Consignee</th>
                                <th>From Location</th>
                                <th>Amount (Rs.)</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredInvoices.map((inv) => (
                                <tr key={inv.id}>
                                    <td className="font-medium text-primary">{inv.invoiceNo}</td>
                                    <td>{inv.date}</td>
                                    <td>{inv.consigneeName}</td>
                                    <td>{inv.fromLocation}</td>
                                    <td className="font-medium font-bold">₹{inv.taxPayable}</td>
                                    <td>
                                        <div className="action-buttons">
                                            <button
                                                onClick={() => downloadFile(inv.id, 'pdf')}
                                                className="btn-action btn-pdf"
                                                title="Download PDF"
                                            >
                                                📄 PDF
                                            </button>
                                            <button
                                                onClick={() => downloadFile(inv.id, 'excel')}
                                                className="btn-action btn-excel"
                                                title="Download Excel"
                                            >
                                                📊 Excel
                                            </button>
                                            <button
                                                onClick={() => handlePrint(inv.id)}
                                                className="btn-action btn-print"
                                                title="Print PDF"
                                            >
                                                🖨️ Print
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default History;
