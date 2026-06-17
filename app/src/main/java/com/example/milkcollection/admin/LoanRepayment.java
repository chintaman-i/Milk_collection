package com.example.milkcollection.model;

public class LoanRepayment {

    private String repaymentId;
    private String loanId;
    private String uid;
    private String farmerName;

    private Double amountPaid;
    private Double previousRemaining;
    private Double newRemaining;

    private Long paymentDate;

    private String enteredBy;

    public LoanRepayment() {
    }

    public String getRepaymentId() {
        return repaymentId;
    }

    public void setRepaymentId(String repaymentId) {
        this.repaymentId = repaymentId;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getPreviousRemaining() {
        return previousRemaining;
    }

    public void setPreviousRemaining(Double previousRemaining) {
        this.previousRemaining = previousRemaining;
    }

    public Double getNewRemaining() {
        return newRemaining;
    }

    public void setNewRemaining(Double newRemaining) {
        this.newRemaining = newRemaining;
    }

    public Long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }
}