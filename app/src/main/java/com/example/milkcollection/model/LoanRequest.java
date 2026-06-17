package com.example.milkcollection.model;

public class LoanRequest {

    private String loanId;
    private String farmerName;
    private String loanStatus;
    private String purpose;
    private String uid;
    private String requestDate;
    private String adminRemark;

    private Double loanAmount;
    private Double approvedAmount;
    private Double remainingAmount;
    private Double paidAmount;
    private Double interestRate;

    // Repayment fields
    private Long totalRepayments;

    private Long createdAt;
    private Long approvalDate;
    private Long durationMonths;
    private Long nextDueDate;

    private Boolean interestEnabled;

    public LoanRequest() {
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Long getTotalRepayments() {
        return totalRepayments;
    }

    public void setTotalRepayments(Long totalRepayments) {
        this.totalRepayments = totalRepayments;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Long approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Long getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Long durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Long getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(Long nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public Boolean getInterestEnabled() {
        return interestEnabled;
    }

    public void setInterestEnabled(Boolean interestEnabled) {
        this.interestEnabled = interestEnabled;
    }
}