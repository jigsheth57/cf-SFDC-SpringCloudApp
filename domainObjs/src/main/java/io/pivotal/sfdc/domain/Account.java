package io.pivotal.sfdc.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SFDC Account Object representation
 * @author Jignesh Sheth
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Account {

	@JsonProperty(value="Id")
	String id;
	@JsonProperty(value="Name")
	String name;
	@JsonProperty(value="Type")
	String type;	
	@JsonProperty(value="Description")
	String description;
	@JsonProperty(value="Industry")
	String industry;
	@JsonProperty(value="Ownership")
	String ownership;
	@JsonProperty(value="Website")
	String website;
	@JsonProperty(value="Phone")
	String phone;
	@JsonProperty(value="NumberOfEmployees")
	Long NumberOfEmployees;
	
	@JsonProperty("Opportunities")
	List<Opportunity> opportunities;

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
		return NumberOfEmployees;
	}
	/**
	 * @param numberOfEmployees the numberOfEmployees to set
	 */
	public void setNumberOfEmployees(Long numberOfEmployees) {
		NumberOfEmployees = numberOfEmployees;
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
