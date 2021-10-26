/*
 * This file is generated by jOOQ.
 */
package io.prhunter.generated.tables.records;


import io.prhunter.generated.tables.Installation;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class InstallationRecord extends UpdatableRecordImpl<InstallationRecord> implements Record6<Long, Long, String, Long, String, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.installation.id</code>.
     */
    public InstallationRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.installation.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.installation.account_id</code>.
     */
    public InstallationRecord setAccountId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.installation.account_id</code>.
     */
    public Long getAccountId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.installation.account_type</code>.
     */
    public InstallationRecord setAccountType(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.installation.account_type</code>.
     */
    public String getAccountType() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.installation.sender_id</code>.
     */
    public InstallationRecord setSenderId(Long value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.installation.sender_id</code>.
     */
    public Long getSenderId() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>public.installation.sender_type</code>.
     */
    public InstallationRecord setSenderType(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>public.installation.sender_type</code>.
     */
    public String getSenderType() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.installation.created_at</code>.
     */
    public InstallationRecord setCreatedAt(LocalDateTime value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>public.installation.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, String, Long, String, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, Long, String, Long, String, LocalDateTime> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Installation.INSTALLATION.ID;
    }

    @Override
    public Field<Long> field2() {
        return Installation.INSTALLATION.ACCOUNT_ID;
    }

    @Override
    public Field<String> field3() {
        return Installation.INSTALLATION.ACCOUNT_TYPE;
    }

    @Override
    public Field<Long> field4() {
        return Installation.INSTALLATION.SENDER_ID;
    }

    @Override
    public Field<String> field5() {
        return Installation.INSTALLATION.SENDER_TYPE;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return Installation.INSTALLATION.CREATED_AT;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getAccountId();
    }

    @Override
    public String component3() {
        return getAccountType();
    }

    @Override
    public Long component4() {
        return getSenderId();
    }

    @Override
    public String component5() {
        return getSenderType();
    }

    @Override
    public LocalDateTime component6() {
        return getCreatedAt();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getAccountId();
    }

    @Override
    public String value3() {
        return getAccountType();
    }

    @Override
    public Long value4() {
        return getSenderId();
    }

    @Override
    public String value5() {
        return getSenderType();
    }

    @Override
    public LocalDateTime value6() {
        return getCreatedAt();
    }

    @Override
    public InstallationRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public InstallationRecord value2(Long value) {
        setAccountId(value);
        return this;
    }

    @Override
    public InstallationRecord value3(String value) {
        setAccountType(value);
        return this;
    }

    @Override
    public InstallationRecord value4(Long value) {
        setSenderId(value);
        return this;
    }

    @Override
    public InstallationRecord value5(String value) {
        setSenderType(value);
        return this;
    }

    @Override
    public InstallationRecord value6(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public InstallationRecord values(Long value1, Long value2, String value3, Long value4, String value5, LocalDateTime value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached InstallationRecord
     */
    public InstallationRecord() {
        super(Installation.INSTALLATION);
    }

    /**
     * Create a detached, initialised InstallationRecord
     */
    public InstallationRecord(Long id, Long accountId, String accountType, Long senderId, String senderType, LocalDateTime createdAt) {
        super(Installation.INSTALLATION);

        setId(id);
        setAccountId(accountId);
        setAccountType(accountType);
        setSenderId(senderId);
        setSenderType(senderType);
        setCreatedAt(createdAt);
    }
}