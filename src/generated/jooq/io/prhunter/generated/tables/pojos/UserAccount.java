/*
 * This file is generated by jOOQ.
 */
package io.prhunter.generated.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String firebaseUserId;
    private final Long   githubUserId;
    private final String githubAccessToken;
    private final String ethWalletAddress;

    public UserAccount(UserAccount value) {
        this.firebaseUserId = value.firebaseUserId;
        this.githubUserId = value.githubUserId;
        this.githubAccessToken = value.githubAccessToken;
        this.ethWalletAddress = value.ethWalletAddress;
    }

    public UserAccount(
        String firebaseUserId,
        Long   githubUserId,
        String githubAccessToken,
        String ethWalletAddress
    ) {
        this.firebaseUserId = firebaseUserId;
        this.githubUserId = githubUserId;
        this.githubAccessToken = githubAccessToken;
        this.ethWalletAddress = ethWalletAddress;
    }

    /**
     * Getter for <code>public.user_account.firebase_user_id</code>.
     */
    public String getFirebaseUserId() {
        return this.firebaseUserId;
    }

    /**
     * Getter for <code>public.user_account.github_user_id</code>.
     */
    public Long getGithubUserId() {
        return this.githubUserId;
    }

    /**
     * Getter for <code>public.user_account.github_access_token</code>.
     */
    public String getGithubAccessToken() {
        return this.githubAccessToken;
    }

    /**
     * Getter for <code>public.user_account.eth_wallet_address</code>.
     */
    public String getEthWalletAddress() {
        return this.ethWalletAddress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("UserAccount (");

        sb.append(firebaseUserId);
        sb.append(", ").append(githubUserId);
        sb.append(", ").append(githubAccessToken);
        sb.append(", ").append(ethWalletAddress);

        sb.append(")");
        return sb.toString();
    }
}