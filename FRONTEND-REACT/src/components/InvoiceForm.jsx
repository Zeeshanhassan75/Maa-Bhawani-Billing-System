import React, { useState } from 'react';
import PdfPreviewRenderer from './PdfPreviewRenderer';
import './InvoiceForm.css';

const getTodayDateString = () => {
    const d = new Date();
    let month = '' + (d.getMonth() + 1);
    let day = '' + d.getDate();
    const year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}

function convertNumberToWords(amount) {
    if (!amount || amount == 0) return "Zero Only";
    const num = Math.floor(parseFloat(amount));
    if (isNaN(num)) return "";
    const a = ['', 'One ', 'Two ', 'Three ', 'Four ', 'Five ', 'Six ', 'Seven ', 'Eight ', 'Nine ', 'Ten ', 'Eleven ', 'Twelve ', 'Thirteen ', 'Fourteen ', 'Fifteen ', 'Sixteen ', 'Seventeen ', 'Eighteen ', 'Nineteen '];
    const b = ['', '', 'Twenty', 'Thirty', 'Forty', 'Fifty', 'Sixty', 'Seventy', 'Eighty', 'Ninety'];

    let numStr = num.toString();
    if (numStr.length > 9) return 'overflow';
    const n = ('000000000' + numStr).substr(-9).match(/^(\d{2})(\d{2})(\d{2})(\d{1})(\d{2})$/);
    if (!n) return "";
    let str = '';
    str += (n[1] != 0) ? (a[Number(n[1])] || b[n[1][0]] + ' ' + a[n[1][1]]) + 'Crore ' : '';
    str += (n[2] != 0) ? (a[Number(n[2])] || b[n[2][0]] + ' ' + a[n[2][1]]) + 'Lakh ' : '';
    str += (n[3] != 0) ? (a[Number(n[3])] || b[n[3][0]] + ' ' + a[n[3][1]]) + 'Thousand ' : '';
    str += (n[4] != 0) ? (a[Number(n[4])] || b[n[4][0]] + ' ' + a[n[4][1]]) + 'Hundred ' : '';
    str += (n[5] != 0) ? ((str != '') ? 'and ' : '') + (a[Number(n[5])] || b[n[5][0]] + ' ' + a[n[5][1]]) : '';
    return str.trim() + " Only";
}

const defaultInvoiceState = {
    invoiceNo: '',
    date: getTodayDateString(),
    modeOfTransport: 'By Road',
    consigneeName: 'INDIAN EXPLOSIVES PRIVATE LIMITED',
    consigneeAddress: 'GOMIA BOKARO 829112, JHARKHAND',
    consigneeGstin: '20AAACI6548N1ZG',
    consigneeCode: '20',
    fromLocation: 'GOMIA',
    toLocation: 'GOMIA',
    challanItems: [
        { challanNo: '1', date: getTodayDateString(), truckNo: '', hsnCode: '9965', weight: '', ratePerTr: '500.00', amount: '' }
    ],
    bankName: 'Bank of India',
    bankBranch: 'Gomia',
    bankAccountNo: '481120110000466',
    bankIfscCode: 'BKID0004811',
    totalInvoiceValue: '',
    invoiceValueInWords: '',
    taxableValue: '',
    grossFreight: '',
    igstPercentage: '5',
    igstAmount: '',
    taxPayable: '',
    includeGstInWords: false,
    customLabels: {
        headerMobileEmail: 'Mob-9934115761\nE mail -Rajan20f@gmail.com',
        headerTaxInvoice: 'TAX INVOICE',
        headerCopies: 'Original for Buyer\nDuplicate for Transporter\nTriplicate for Supporter',
        companyName: 'MAA BHAWANI TRADERS',
        companyAddress: 'Near Bijli Office,BDO Road Gomia,P.O- IE Gomia Dist-Bokaro',
        companyGstin: 'GSTIN/UIN: 20AOFPY3578Q1Z5 PAN number : AOFPY3578Q State Name : Jharkhand, Code : 20',
        companyPo: 'PO NO.4500777226',
        consigneeTitle: 'Name & Address of the Consignee:',
        invoiceTitle: 'Invoice No.: ',
        invoiceModeTitle: 'Mode of Transportation: ',
        invoiceDateTitle: 'Date: ',
        fromTitle: 'FROM: ',
        toTitle: 'TO: ',
        bankDetailsTitle: 'Bank Details:',
        totalInvoiceValueTitle: 'Total Invoice Value:',
        invoiceValueInWordsTitle: 'Invoice Value in Words: Rs. ',
        creditInputText: 'Credit on input tax on goods and services used in supplying the services has not been taken.',
        taxableValueTitle: 'TAXABLE VALUE',
        grossFreightTitle: 'GROSS FREIGHT:',
        igstTitle: 'IGST @ ',
        taxPayableTitle: 'TAX PAYABLE BY CONSIGNEE:',
        declarationTitle: 'Declaration:',
        declarationText: 'We hereby certify that Cenvat credit on input services used for providing taxable\nservice, has not been taken under the provisions on Cenvat Credit rules 2004.\nThe GST is to be paid under reverse charge mechanism by the recipient\nof service.',
        forCompanyTitle: 'For: MAA BHAWANI TRADERS',
        authSignatoryTitle: 'Authorised signatory'
    }
};

const InvoiceForm = ({ onViewHistory }) => {
    const [formData, setFormData] = useState(defaultInvoiceState);
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [createdInvoiceNo, setCreatedInvoiceNo] = useState('');
    const [showModal, setShowModal] = useState(false);

    // Auto calculation logic
    const handleItemChange = (index, field, value) => {
        const newItems = [...formData.challanItems];
        newItems[index][field] = value;

        if (field === 'weight' || field === 'ratePerTr') {
            const weight = parseFloat(newItems[index].weight) || 0;
            const rate = parseFloat(newItems[index].ratePerTr) || 0;
            if (weight > 0 && rate > 0) {
                newItems[index].amount = (weight * rate).toFixed(2);
            }
        }

        setFormData(prev => {
            const updated = { ...prev, challanItems: newItems };
            return recalculateTotals(updated);
        });
    };

    const recalculateTotals = (data) => {
        const totalAmount = data.challanItems.reduce((sum, item) => sum + (parseFloat(item.amount) || 0), 0);
        const taxableValue = totalAmount;
        const igstAmount = (taxableValue * (parseFloat(data.igstPercentage) / 100));
        const totalPayable = taxableValue + igstAmount;

        return {
            ...data,
            totalInvoiceValue: taxableValue.toFixed(2),
            taxableValue: taxableValue.toFixed(2),
            grossFreight: taxableValue.toFixed(2),
            igstAmount: igstAmount.toFixed(2),
            taxPayable: totalPayable.toFixed(2),
            invoiceValueInWords: convertNumberToWords(data.includeGstInWords ? totalPayable : taxableValue)
        };
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        handleFieldChange(name, value);
    };

    const handleFieldChange = (name, value) => {
        setFormData(prev => recalculateTotals({ ...prev, [name]: value }));
    };

    const handleLabelChange = (name, value) => {
        setFormData(prev => ({
            ...prev,
            customLabels: {
                ...prev.customLabels,
                [name]: value
            }
        }));
    };

    const addItemRow = () => {
        setFormData(prev => {
            let nextChallanNo = '1';
            const lastItem = prev.challanItems.length > 0 ? prev.challanItems[prev.challanItems.length - 1] : null;
            if (lastItem && lastItem.challanNo) {
                const num = parseInt(lastItem.challanNo, 10);
                if (!isNaN(num)) {
                    nextChallanNo = (num + 1).toString();
                }
            }
            return {
                ...prev,
                challanItems: [...prev.challanItems, { challanNo: nextChallanNo, date: prev.date, truckNo: '', hsnCode: '9965', weight: '', ratePerTr: '500.00', amount: '' }]
            };
        });
    };

    const removeItemRow = (index) => {
        setFormData(prev => {
            const items = prev.challanItems.filter((_, i) => i !== index);
            return recalculateTotals({ ...prev, challanItems: items });
        });
    };

    const handlePreSave = (e) => {
        e.preventDefault();
        setShowModal(true);
    };

    const confirmSave = async () => {
        setShowModal(false);
        setLoading(true);
        setSuccess(false);

        // Format dates to DD.MM.YYYY for backend/PDF before saving
        const formattedData = {
            ...formData,
            date: formatDate(formData.date),
            challanItems: formData.challanItems.map(item => ({
                ...item,
                date: formatDate(item.date)
            }))
        };

        try {
            const res = await fetch('http://localhost:8080/api/invoices', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(formattedData)
            });
            if (res.ok) {
                setCreatedInvoiceNo(formData.invoiceNo);
                setSuccess(true);
                setFormData(defaultInvoiceState);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        if (dateStr.includes('-')) {
            const parts = dateStr.split('-');
            return `${parts[2]}.${parts[1]}.${parts[0]}`;
        }
        return dateStr;
    }

    return (
        <div className="invoice-split-layout">
            {/* Left Pane: Form Editor */}
            <div className="invoice-form-pane hide-on-print">
                <div className="page-header">
                    <h1 className="page-title">Create New Bill</h1>
                </div>

                <form onSubmit={handlePreSave} className="invoice-form">
                    <section className="form-section glass-panel">
                        <h2>Basic Details</h2>
                        <div className="form-grid-3">
                            <div className="input-group">
                                <label>Invoice No</label>
                                <input name="invoiceNo" value={formData.invoiceNo} onChange={handleChange} required placeholder="C18/26-27" />
                            </div>
                            <div className="input-group">
                                <label>Date</label>
                                <input type="date" name="date" value={formData.date} onChange={handleChange} required />
                            </div>
                            <div className="input-group">
                                <label>Mode of Transport</label>
                                <input name="modeOfTransport" value={formData.modeOfTransport} onChange={handleChange} />
                            </div>
                        </div>
                    </section>

                    <section className="form-section glass-panel">
                        <h2>Consignee Details</h2>
                        <div className="form-grid-2">
                            <div className="input-group">
                                <label>Name</label>
                                <input name="consigneeName" value={formData.consigneeName} onChange={handleChange} />
                            </div>
                            <div className="input-group">
                                <label>Address</label>
                                <input name="consigneeAddress" value={formData.consigneeAddress} onChange={handleChange} />
                            </div>
                            <div className="input-group">
                                <label>GSTIN / UIN</label>
                                <input name="consigneeGstin" value={formData.consigneeGstin} onChange={handleChange} />
                            </div>
                            <div className="input-group">
                                <label>Code</label>
                                <input name="consigneeCode" value={formData.consigneeCode} onChange={handleChange} />
                            </div>
                        </div>
                    </section>

                    <section className="form-section glass-panel">
                        <div className="form-grid-2">
                            <div className="input-group">
                                <label>From</label>
                                <input name="fromLocation" value={formData.fromLocation} onChange={handleChange} />
                            </div>
                            <div className="input-group">
                                <label>To</label>
                                <input name="toLocation" value={formData.toLocation} onChange={handleChange} />
                            </div>
                        </div>
                    </section>

                    <section className="form-section glass-panel items-section">
                        <div className="section-header">
                            <h2>Challan Items</h2>
                            <button type="button" onClick={addItemRow} className="btn-secondary">+ Add Row</button>
                        </div>

                        <div className="table-responsive">
                            <table className="items-table">
                                <thead>
                                    <tr>
                                        <th>CHALLAN NO</th>
                                        <th>DATE</th>
                                        <th>TRUCK NO</th>
                                        <th>HSN Code</th>
                                        <th>Weight</th>
                                        <th>RATE PER TR</th>
                                        <th>AMOUNT</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {formData.challanItems.map((item, index) => (
                                        <tr key={index}>
                                            <td><input value={item.challanNo} onChange={(e) => handleItemChange(index, 'challanNo', e.target.value)} required /></td>
                                            <td><input type="date" value={item.date} onChange={(e) => handleItemChange(index, 'date', e.target.value)} /></td>
                                            <td><input value={item.truckNo} onChange={(e) => handleItemChange(index, 'truckNo', e.target.value)} /></td>
                                            <td><input value={item.hsnCode} onChange={(e) => handleItemChange(index, 'hsnCode', e.target.value)} /></td>
                                            <td><input type="number" step="0.01" value={item.weight} onChange={(e) => handleItemChange(index, 'weight', e.target.value)} required /></td>
                                            <td><input type="number" step="0.01" value={item.ratePerTr} onChange={(e) => handleItemChange(index, 'ratePerTr', e.target.value)} required /></td>
                                            <td><input value={item.amount} readOnly className="readonly-input" /></td>
                                            <td>
                                                {formData.challanItems.length > 1 && (
                                                    <button type="button" onClick={() => removeItemRow(index)} className="btn-icon">❌</button>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <section className="form-section glass-panel">
                        <h2>Summary & Tax</h2>
                        <div className="form-grid-2">
                            <div className="input-group full-width">
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <label>Invoice Value in Words (Auto)</label>
                                    <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', fontWeight: 'normal', fontSize: '0.85rem' }}>
                                        <input type="checkbox" name="includeGstInWords" checked={formData.includeGstInWords} onChange={(e) => handleFieldChange('includeGstInWords', e.target.checked)} style={{ width: 'auto' }} />
                                        Include GST in WORDS
                                    </label>
                                </div>
                                <input name="invoiceValueInWords" value={formData.invoiceValueInWords} onChange={handleChange} placeholder="e.g. Fourty Three Thousands ..." required />
                            </div>

                            <div className="totals-box">
                                <div className="total-row"><span>Total Invoice Value:</span> <strong>{formData.totalInvoiceValue}</strong></div>
                                <div className="total-row"><span>Taxable Value:</span> <strong>{formData.taxableValue}</strong></div>
                                <div className="total-row"><span>Gross Freight:</span> <strong>{formData.grossFreight}</strong></div>
                                <div className="total-row">
                                    <span>IGST @ <input type="number" className="inline-input" name="igstPercentage" value={formData.igstPercentage} onChange={handleChange} />%:</span>
                                    <strong>{formData.igstAmount}</strong>
                                </div>
                                <div className="total-row grand-total"><span>TAX PAYABLE BY CONSIGNEE:</span> <strong>{formData.taxPayable}</strong></div>
                            </div>
                        </div>
                    </section>

                    <div className="form-actions sticky-actions">
                        <button type="submit" className="btn-primary" disabled={loading}>
                            Review & Save Bill
                        </button>
                    </div>
                </form>
            </div>

            {/* Right Pane: Live PDF Preview */}
            <div className="invoice-preview-pane">
                <div className="hide-on-print">
                    <h2 className="preview-heading">Live PDF Preview</h2>
                    <p className="preview-subheading">Full A4 Layout. Click on any text inside the document (like Headers, Footers or Data) to seamlessly rename them in real-time!</p>
                </div>
                <PdfPreviewRenderer
                    formData={formData}
                    onFieldChange={handleFieldChange}
                    onChallanChange={handleItemChange}
                    onLabelChange={handleLabelChange}
                />
            </div>

            {/* Confirmation Modal */}
            {showModal && (
                <div className="modal-overlay hide-on-print">
                    <div className="modal-content glass-panel">
                        <div className="modal-header">
                            <h2 className="modal-title">Confirm Final Invoice</h2>
                        </div>
                        <p className="modal-desc">Please review the final document format before saving. <strong>Note: Dates will be formatted to DD.MM.YYYY internally.</strong></p>
                        <div className="modal-scroll-area">
                            <PdfPreviewRenderer
                                formData={formData}
                                readOnly={true}
                            />
                        </div>
                        <div className="modal-actions">
                            <button className="btn-secondary" onClick={() => setShowModal(false)}>Back to Edit</button>
                            <button className="btn-primary" onClick={confirmSave}>Confirm & Save Bill</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Success Animation Modal */}
            {success && (
                <div className="modal-overlay" style={{ zIndex: 1000 }}>
                    <div className="modal-content glass-panel success-modal" style={{ alignItems: 'center', maxWidth: '400px', height: 'auto', padding: '3rem', textAlign: 'center' }}>
                        <div className="success-checkmark">
                            <div className="check-icon">
                                <span className="icon-line line-tip"></span>
                                <span className="icon-line line-long"></span>
                                <div className="icon-circle"></div>
                                <div className="icon-fix"></div>
                            </div>
                        </div>
                        <h2 style={{ color: '#166534', marginTop: '1.5rem', fontSize: '1.5rem' }}>Bill Created!</h2>
                        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', marginBottom: '2rem' }}>
                            Invoice No: <strong>{createdInvoiceNo}</strong> has been successfully generated.
                        </p>
                        <button className="btn-primary" onClick={() => { setSuccess(false); if (onViewHistory) onViewHistory(); }} style={{ width: '100%', padding: '0.75rem' }}>
                            View in Bill History
                        </button>
                    </div>
                </div>
            )}

        </div>
    );
};

export default InvoiceForm;
