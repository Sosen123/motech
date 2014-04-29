package org.motechproject.hub.model.hibernate;

// Generated Apr 21, 2014 1:53:24 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * HubTopic generated by hbm2java
 */
@Entity
@Table(name = "hub_topic", schema = "hub")
public class HubTopic implements java.io.Serializable {

	private static final long serialVersionUID = -5048963496204264339L;
	
	private long topicId;
	private String topicUrl;
	private Date createTime;
	private Date lastUpdated;
	private String createdBy;
	private String lastUpdatedBy;
	private Set<HubSubscription> hubSubscriptions = new HashSet<HubSubscription>(
			0);
	private Set<HubPublisherTransaction> hubPublisherTransactions = new HashSet<HubPublisherTransaction>(
			0);

	public HubTopic() {
	}

	public HubTopic(long topicId, String topicUrl) {
		this.topicId = topicId;
		this.topicUrl = topicUrl;
	}

	public HubTopic(long topicId, String topicUrl, Date createTime,
			Date lastUpdated, String createdBy, String lastUpdatedBy,
			Set<HubSubscription> hubSubscriptions,
			Set<HubPublisherTransaction> hubPublisherTransactions) {
		this.topicId = topicId;
		this.topicUrl = topicUrl;
		this.createTime = createTime;
		this.lastUpdated = lastUpdated;
		this.createdBy = createdBy;
		this.lastUpdatedBy = lastUpdatedBy;
		this.hubSubscriptions = hubSubscriptions;
		this.hubPublisherTransactions = hubPublisherTransactions;
	}

	@Id
	@Column(name = "topic_id", unique = true, nullable = false)
	public long getTopicId() {
		return this.topicId;
	}

	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}

	@Column(name = "topic_url", nullable = false)
	public String getTopicUrl() {
		return this.topicUrl;
	}

	public void setTopicUrl(String topicUrl) {
		this.topicUrl = topicUrl;
	}

	@Temporal(TemporalType.TIME)
	@Column(name = "create_time", length = 15)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIME)
	@Column(name = "last_updated", length = 15)
	public Date getLastUpdated() {
		return this.lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Column(name = "created_by", length = 100)
	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Column(name = "last_updated_by", length = 100)
	public String getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hubTopic")
	public Set<HubSubscription> getHubSubscriptions() {
		return this.hubSubscriptions;
	}

	public void setHubSubscriptions(Set<HubSubscription> hubSubscriptions) {
		this.hubSubscriptions = hubSubscriptions;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hubTopic")
	public Set<HubPublisherTransaction> getHubPublisherTransactions() {
		return this.hubPublisherTransactions;
	}

	public void setHubPublisherTransactions(
			Set<HubPublisherTransaction> hubPublisherTransactions) {
		this.hubPublisherTransactions = hubPublisherTransactions;
	}

}