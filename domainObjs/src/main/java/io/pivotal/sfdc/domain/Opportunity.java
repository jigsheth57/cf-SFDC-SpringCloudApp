package io.pivotal.sfdc.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * SFDC Opportunity Object representation
 * @author Jignesh Sheth
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Opportunity {
	
	@JsonView(View.OpportunityByAccountSummary.class)
	@JsonProperty(value="Id")
	String id;
	@JsonView(View.OpportunityByAccountSummary.class)
	@JsonProperty(value="Name")
	String name;
	@JsonProperty(value="Description")
	String description;
	@JsonProperty(value="NextStep")
	String nextStep;
	@JsonProperty(value="LeadSource")
	String leadSource;
	@JsonProperty(value="Type")
	String type;	
	@JsonProperty(value="Amount")
	Double amount;
	@JsonProperty(value="StageName")
	String stagename;	
	@JsonProperty(value="CloseDate")
	Date closedate;	
	@JsonProperty("AccountId")
	String accountId;
    @JsonProperty("Account")
    Account account;
	
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getStagename() {
		return stagename;
	}

	public void setStagename(String stagename) {
		this.stagename = stagename;
	}

	public Date getClosedate() {
		return closedate;
	}

	public void setClosedate(Date closedate) {
		this.closedate = closedate;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the nextStep
	 */
	public String getNextStep() {
		return nextStep;
	}
	/**
	 * @param nextStep the nextStep to set
	 */
	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}
	/**
	 * @return the leadSource
	 */
	public String getLeadSource() {
		return leadSource;
	}
	/**
	 * @param leadSource the leadSource to set
	 */
	public void setLeadSource(String leadSource) {
		this.leadSource = leadSource;
	}
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
