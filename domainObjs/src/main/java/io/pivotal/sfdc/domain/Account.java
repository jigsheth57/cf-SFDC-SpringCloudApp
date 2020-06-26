package io.pivotal.sfdc.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * SFDC Account Object representation
 * 
 * @author Jignesh Sheth
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

	@JsonView(View.Summary.class)
	@JsonProperty(value = "Id")
	String id;

	@JsonView(View.Summary.class)
	@JsonProperty(value = "Name")
	String name;

	@JsonView(View.Summary.class)
	@JsonProperty(value = "Type")
	String type;

	@JsonView(View.AccountDetailSummary.class)
	@JsonProperty(value = "Description")
	String description;

	@JsonView(View.AccountDetailSummary.class)
	@JsonProperty(value = "Industry")
	String industry;

	@JsonView(View.AccountDetailSummary.class)
	@JsonProperty(value = "Ownership")
	String ownership;

	@JsonView(View.AccountDetailSummary.class)
	@JsonProperty(value = "Website")
	String website;

	@JsonView(View.AccountDetailSummary.class)
	@JsonProperty(value = "Phone")
	String phone;

	@JsonView(View.AccountDetailSummary.class)
	@JsonProperty(value = "NumberOfEmployees")
	Long numberOfEmployees;

	@JsonView(View.OpportunityByAccountSummary.class)
	@JsonProperty("Opportunities")
	List<Opportunity> opportunities;

	@JsonView(View.ContactByAccountSummary.class)
	@JsonProperty("Contacts")
	List<Contact> contacts;

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
	 * @return the industry
	 */
	public String getIndustry() {
		return industry;
	}

	/**
	 * @param industry the industry to set
	 */
	public void setIndustry(String industry) {
		this.industry = industry;
	}

	/**
	 * @return the ownership
	 */
	public String getOwnership() {
		return ownership;
	}

	/**
	 * @param ownership the ownership to set
	 */
	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}

	/**
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * @param website the website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the numberOfEmployees
	 */
	public Long getNumberOfEmployees() {
		return numberOfEmployees;
	}

	/**
	 * @param numberOfEmployees the numberOfEmployees to set
	 */
	public void setNumberOfEmployees(Long numberOfEmployees) {
		this.numberOfEmployees = numberOfEmployees;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<Opportunity> getOpportunities() {
		return opportunities;
	}

	public void setOpportunities(List<Opportunity> opportunities) {
		this.opportunities = opportunities;
	}

}
