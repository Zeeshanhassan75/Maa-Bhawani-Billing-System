import React, { useRef, useEffect, useState } from 'react';
import './PdfPreviewRenderer.css';

const PdfPreviewRenderer = ({ formData, onFieldChange, onChallanChange, onLabelChange, readOnly = false }) => {
    const handleChange = (e) => {
        if (readOnly) return;
        const { name, value } = e.target;
        onFieldChange(name, value);
    };

    const handleItemChange = (index, field, value) => {
        if (readOnly) return;
        onChallanChange(index, field, value);
    };

    const handleLabelChange = (e) => {
        if (readOnly) return;
        const { name, value } = e.target;
        onLabelChange(name, value);
    };

    const InputCmp = ({ name, value, className, type = "text" }) => {
        if (readOnly) {
            return <span className={`preview-text ${className}`}>{value}</span>;
        }
        return (
            <input
                type={type}
                name={name}
                value={value}
                onChange={handleChange}
                className={`preview-input ${className}`}
                autoComplete="off"
            />
        );
    };

    const LabelCmp = ({ name, className, inline = false }) => {
        const value = formData.customLabels[name] || '';
        if (readOnly) {
            return <span className={`preview-text ${className}`}>{value}</span>;
        }
        if (inline) {
            return <input name={name} value={value} onChange={handleLabelChange} className={`preview-input ${className}`} autoComplete="off" />;
        }
        return (
            <textarea
                name={name}
                value={value}
                onChange={handleLabelChange}
                className={`preview-input preview-textarea ${className}`}
                autoComplete="off"
                rows={value.split('\\n').length}
            />
        );
    };

    return (
        <div className="pdf-preview-container" id="pdf-printable-area">
            <div className="pdf-page-wrapper">
                <div className="pdf-page">
                    {/* Header Grid */}
                    <div className="pdf-header-grid">
                        <div className="pdf-cell align-left">
                            <strong><LabelCmp name="headerMobileEmail" /></strong>
                        </div>
                        <div className="pdf-cell align-center pdf-tax-title-container">
                            <h2 className="pdf-tax-title" style={{ textDecoration: 'underline' }}><LabelCmp name="headerTaxInvoice" inline /></h2>
                        </div>
                        <div className="pdf-cell align-right">
                            <LabelCmp name="headerCopies" />
                        </div>
                    </div>

                    {/* Company Title */}
                    <div className="pdf-company-section">
                        <div className="pdf-company-name-container">
                            <LabelCmp name="companyName" inline />
                        </div>
                        <p className="pdf-company-address"><strong><LabelCmp name="companyAddress" inline className="w-full text-center" /></strong></p>
                        <p className="pdf-company-gstin"><strong><LabelCmp name="companyGstin" inline className="w-full text-center" /></strong></p>
                        <p className="pdf-po-no"><strong><LabelCmp name="companyPo" inline className="w-full text-right" /></strong></p>
                    </div>

                    {/* Details Grid */}
                    <div className="pdf-details-grid">
                        <div className="pdf-cell">
                            <strong><LabelCmp name="consigneeTitle" inline className="w-full" /></strong><br />
                            <InputCmp name="consigneeName" value={formData.consigneeName} className="w-full" /><br />
                            <InputCmp name="consigneeAddress" value={formData.consigneeAddress} className="w-full" /><br />
                            GSTIN/UIN :: <InputCmp name="consigneeGstin" value={formData.consigneeGstin} className="inline-input-preview w-40" />
                            Code : <InputCmp name="consigneeCode" value={formData.consigneeCode} className="inline-input-preview w-20" />
                        </div>
                        <div className="pdf-cell">
                            <strong><LabelCmp name="invoiceTitle" inline className="w-20" /> <InputCmp name="invoiceNo" value={formData.invoiceNo} className="inline-input-preview w-32" /></strong><br />
                            <LabelCmp name="invoiceModeTitle" inline className="w-48" /> <InputCmp name="modeOfTransport" value={formData.modeOfTransport} className="inline-input-preview w-32" /><br />
                            <strong><LabelCmp name="invoiceDateTitle" inline className="w-16" /> <InputCmp name="date" value={formData.date} type="date" className="inline-input-preview" /></strong>
                        </div>

                        <div className="pdf-cell">
                            <strong><LabelCmp name="fromTitle" inline className="w-16" /> <InputCmp name="fromLocation" value={formData.fromLocation} className="inline-input-preview uppercase" /></strong>
                        </div>
                        <div className="pdf-cell">
                            <strong><LabelCmp name="toTitle" inline className="w-16" /> <InputCmp name="toLocation" value={formData.toLocation} className="inline-input-preview uppercase" /></strong>
                        </div>
                    </div>

                    {/* Items Table */}
                    <table className="pdf-items-table">
                        <thead>
                            <tr>
                                <th style={{ width: '12%' }}>CHALLAN NO</th>
                                <th style={{ width: '15%' }}>DATE</th>
                                <th style={{ width: '15%' }}>TRUCK NO.</th>
                                <th style={{ width: '12%' }}>HSN Code</th>
                                <th style={{ width: '12%' }}>Weight</th>
                                <th style={{ width: '16%' }}>RATE PER TR</th>
                                <th style={{ width: '18%' }}>AMOUNT (Rs.)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {formData.challanItems.map((item, i) => (
                                <tr key={i}>
                                    <td>
                                        {readOnly ? item.challanNo : <input value={item.challanNo} onChange={e => handleItemChange(i, 'challanNo', e.target.value)} className="preview-input text-center" />}
                                    </td>
                                    <td>
                                        {readOnly ? item.date : <input type="date" value={item.date} onChange={e => handleItemChange(i, 'date', e.target.value)} className="preview-input text-center" />}
                                    </td>
                                    <td>
                                        {readOnly ? item.truckNo : <input value={item.truckNo} onChange={e => handleItemChange(i, 'truckNo', e.target.value)} className="preview-input text-center" />}
                                    </td>
                                    <td>
                                        {readOnly ? item.hsnCode : <input value={item.hsnCode} onChange={e => handleItemChange(i, 'hsnCode', e.target.value)} className="preview-input text-center" />}
                                    </td>
                                    <td>
                                        {readOnly ? item.weight : <input type="number" step="0.01" value={item.weight} onChange={e => handleItemChange(i, 'weight', e.target.value)} className="preview-input text-center" />}
                                    </td>
                                    <td>
                                        {readOnly ? item.ratePerTr : <input type="number" step="0.01" value={item.ratePerTr} onChange={e => handleItemChange(i, 'ratePerTr', e.target.value)} className="preview-input text-center" />}
                                    </td>
                                    <td className="text-center">{item.amount || '0.00'}</td>
                                </tr>
                            ))}

                            {/* Fillers here if needed */}

                            {/* Bank Details & Total Row */}
                            <tr className="pdf-bank-row">
                                <td colSpan="5" className="align-left">
                                    <strong><LabelCmp name="bankDetailsTitle" inline className="w-32" /></strong><br />
                                    Bank Name: <InputCmp name="bankName" value={formData.bankName} className="inline-input-preview w-40" />,
                                    Branch: <InputCmp name="bankBranch" value={formData.bankBranch} className="inline-input-preview w-32" /><br />
                                    Bank Account No.: <InputCmp name="bankAccountNo" value={formData.bankAccountNo} className="inline-input-preview w-48" /> |
                                    IFSC Code: <InputCmp name="bankIfscCode" value={formData.bankIfscCode} className="inline-input-preview w-32" />
                                </td>
                                <td className="align-right"><strong><LabelCmp name="totalInvoiceValueTitle" inline className="w-full text-right" /></strong></td>
                                <td className="text-center"><strong>{formData.totalInvoiceValue || '0.00'}</strong></td>
                            </tr>
                        </tbody>
                    </table>

                    {/* Invoice Words */}
                    <div className="pdf-words-section">
                        <strong><LabelCmp name="invoiceValueInWordsTitle" inline className="w-48" /> <InputCmp name="invoiceValueInWords" value={formData.invoiceValueInWords} className="inline-input-preview w-full-inline" /></strong>
                    </div>

                    {/* Tax Table */}
                    <div className="pdf-tax-grid">
                        <div className="pdf-tax-left">
                            <strong><LabelCmp name="creditInputText" /></strong>

                            <table className="pdf-inner-tax-table">
                                <tbody>
                                    <tr>
                                        <td className="align-right"><strong><LabelCmp name="taxableValueTitle" inline className="w-48 text-right" /></strong></td>
                                        <td className="text-center">{formData.taxableValue || '0.00'}</td>
                                    </tr>
                                    <tr>
                                        <td className="align-right"><LabelCmp name="grossFreightTitle" inline className="w-48 text-right" /></td>
                                        <td className="text-center">{formData.grossFreight || '0.00'}</td>
                                    </tr>
                                    <tr>
                                        <td className="align-right">
                                            <LabelCmp name="igstTitle" inline className="w-16 text-right" /> <InputCmp name="igstPercentage" value={formData.igstPercentage} type="number" className="inline-input-preview w-16 text-center" />%:
                                        </td>
                                        <td className="text-center">{formData.igstAmount || '0.00'}</td>
                                    </tr>
                                    <tr>
                                        <td className="align-right"><strong><LabelCmp name="taxPayableTitle" inline className="w-full text-right" /></strong></td>
                                        <td className="text-center"><strong>{formData.taxPayable || '0.00'}</strong></td>
                                    </tr>
                                </tbody>
                            </table>

                            <div className="pdf-declaration">
                                <strong><LabelCmp name="declarationTitle" inline className="w-32" /></strong><br />
                                <LabelCmp name="declarationText" />
                            </div>
                        </div>

                        <div className="pdf-tax-right">
                            <strong><LabelCmp name="forCompanyTitle" inline className="w-full text-center" /></strong>
                            <div className="pdf-signature-space"></div>
                            <strong><LabelCmp name="authSignatoryTitle" inline className="w-full text-center" /></strong>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default PdfPreviewRenderer;
