/*
 * This file is generated by jOOQ.
 */
package io.prhunter.generated.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Bounty implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID          id;
    private final Long          repoId;
    private final Long          issueId;
    private final String        title;
    private final String        acceptanceCriteria;
    private final String        problemStatement;
    private final String[]      languages;
    private final BigDecimal    bountyValue;
    private final BigDecimal    bountyValueUsd;
    private final String        bountyCurrency;
    private final String[]      tags;
    private final String        experience;
    private final String        bountyType;
    private final String        firebaseUserId;
    private final Long          issueNumber;
    private final String        repoOwner;
    private final String        repoName;
    private final String        bountyStatus;
    private final String        completedBy;
    private final LocalDateTime completedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private final String        blockchainAddress;

    public Bounty(Bounty value) {
        this.id = value.id;
        this.repoId = value.repoId;
        this.issueId = value.issueId;
        this.title = value.title;
        this.acceptanceCriteria = value.acceptanceCriteria;
        this.problemStatement = value.problemStatement;
        this.languages = value.languages;
        this.bountyValue = value.bountyValue;
        this.bountyValueUsd = value.bountyValueUsd;
        this.bountyCurrency = value.bountyCurrency;
        this.tags = value.tags;
        this.experience = value.experience;
        this.bountyType = value.bountyType;
        this.firebaseUserId = value.firebaseUserId;
        this.issueNumber = value.issueNumber;
        this.repoOwner = value.repoOwner;
        this.repoName = value.repoName;
        this.bountyStatus = value.bountyStatus;
        this.completedBy = value.completedBy;
        this.completedAt = value.completedAt;
        this.createdAt = value.createdAt;
        this.expiresAt = value.expiresAt;
        this.blockchainAddress = value.blockchainAddress;
    }

    public Bounty(
        UUID          id,
        Long          repoId,
        Long          issueId,
        String        title,
        String        acceptanceCriteria,
        String        problemStatement,
        String[]      languages,
        BigDecimal    bountyValue,
        BigDecimal    bountyValueUsd,
        String        bountyCurrency,
        String[]      tags,
        String        experience,
        String        bountyType,
        String        firebaseUserId,
        Long          issueNumber,
        String        repoOwner,
        String        repoName,
        String        bountyStatus,
        String        completedBy,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        String        blockchainAddress
    ) {
        this.id = id;
        this.repoId = repoId;
        this.issueId = issueId;
        this.title = title;
        this.acceptanceCriteria = acceptanceCriteria;
        this.problemStatement = problemStatement;
        this.languages = languages;
        this.bountyValue = bountyValue;
        this.bountyValueUsd = bountyValueUsd;
        this.bountyCurrency = bountyCurrency;
        this.tags = tags;
        this.experience = experience;
        this.bountyType = bountyType;
        this.firebaseUserId = firebaseUserId;
        this.issueNumber = issueNumber;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.bountyStatus = bountyStatus;
        this.completedBy = completedBy;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.blockchainAddress = blockchainAddress;
    }

    /**
     * Getter for <code>public.bounty.id</code>.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Getter for <code>public.bounty.repo_id</code>.
     */
    public Long getRepoId() {
        return this.repoId;
    }

    /**
     * Getter for <code>public.bounty.issue_id</code>.
     */
    public Long getIssueId() {
        return this.issueId;
    }

    /**
     * Getter for <code>public.bounty.title</code>.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Getter for <code>public.bounty.acceptance_criteria</code>.
     */
    public String getAcceptanceCriteria() {
        return this.acceptanceCriteria;
    }

    /**
     * Getter for <code>public.bounty.problem_statement</code>.
     */
    public String getProblemStatement() {
        return this.problemStatement;
    }

    /**
     * Getter for <code>public.bounty.languages</code>.
     */
    public String[] getLanguages() {
        return this.languages;
    }

    /**
     * Getter for <code>public.bounty.bounty_value</code>.
     */
    public BigDecimal getBountyValue() {
        return this.bountyValue;
    }

    /**
     * Getter for <code>public.bounty.bounty_value_usd</code>.
     */
    public BigDecimal getBountyValueUsd() {
        return this.bountyValueUsd;
    }

    /**
     * Getter for <code>public.bounty.bounty_currency</code>.
     */
    public String getBountyCurrency() {
        return this.bountyCurrency;
    }

    /**
     * Getter for <code>public.bounty.tags</code>.
     */
    public String[] getTags() {
        return this.tags;
    }

    /**
     * Getter for <code>public.bounty.experience</code>.
     */
    public String getExperience() {
        return this.experience;
    }

    /**
     * Getter for <code>public.bounty.bounty_type</code>.
     */
    public String getBountyType() {
        return this.bountyType;
    }

    /**
     * Getter for <code>public.bounty.firebase_user_id</code>.
     */
    public String getFirebaseUserId() {
        return this.firebaseUserId;
    }

    /**
     * Getter for <code>public.bounty.issue_number</code>.
     */
    public Long getIssueNumber() {
        return this.issueNumber;
    }

    /**
     * Getter for <code>public.bounty.repo_owner</code>.
     */
    public String getRepoOwner() {
        return this.repoOwner;
    }

    /**
     * Getter for <code>public.bounty.repo_name</code>.
     */
    public String getRepoName() {
        return this.repoName;
    }

    /**
     * Getter for <code>public.bounty.bounty_status</code>.
     */
    public String getBountyStatus() {
        return this.bountyStatus;
    }

    /**
     * Getter for <code>public.bounty.completed_by</code>.
     */
    public String getCompletedBy() {
        return this.completedBy;
    }

    /**
     * Getter for <code>public.bounty.completed_at</code>.
     */
    public LocalDateTime getCompletedAt() {
        return this.completedAt;
    }

    /**
     * Getter for <code>public.bounty.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Getter for <code>public.bounty.expires_at</code>.
     */
    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    /**
     * Getter for <code>public.bounty.blockchain_address</code>.
     */
    public String getBlockchainAddress() {
        return this.blockchainAddress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Bounty (");

        sb.append(id);
        sb.append(", ").append(repoId);
        sb.append(", ").append(issueId);
        sb.append(", ").append(title);
        sb.append(", ").append(acceptanceCriteria);
        sb.append(", ").append(problemStatement);
        sb.append(", ").append(Arrays.toString(languages));
        sb.append(", ").append(bountyValue);
        sb.append(", ").append(bountyValueUsd);
        sb.append(", ").append(bountyCurrency);
        sb.append(", ").append(Arrays.toString(tags));
        sb.append(", ").append(experience);
        sb.append(", ").append(bountyType);
        sb.append(", ").append(firebaseUserId);
        sb.append(", ").append(issueNumber);
        sb.append(", ").append(repoOwner);
        sb.append(", ").append(repoName);
        sb.append(", ").append(bountyStatus);
        sb.append(", ").append(completedBy);
        sb.append(", ").append(completedAt);
        sb.append(", ").append(createdAt);
        sb.append(", ").append(expiresAt);
        sb.append(", ").append(blockchainAddress);

        sb.append(")");
        return sb.toString();
    }
}
