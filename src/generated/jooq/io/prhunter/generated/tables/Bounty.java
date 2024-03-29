/*
 * This file is generated by jOOQ.
 */
package io.prhunter.generated.tables;


import io.prhunter.generated.Keys;
import io.prhunter.generated.Public;
import io.prhunter.generated.tables.records.BountyRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Bounty extends TableImpl<BountyRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.bounty</code>
     */
    public static final Bounty BOUNTY = new Bounty();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BountyRecord> getRecordType() {
        return BountyRecord.class;
    }

    /**
     * The column <code>public.bounty.id</code>.
     */
    public final TableField<BountyRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false).defaultValue(DSL.field("gen_random_uuid()", SQLDataType.UUID)), this, "");

    /**
     * The column <code>public.bounty.repo_id</code>.
     */
    public final TableField<BountyRecord, Long> REPO_ID = createField(DSL.name("repo_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.bounty.issue_id</code>.
     */
    public final TableField<BountyRecord, Long> ISSUE_ID = createField(DSL.name("issue_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.bounty.title</code>.
     */
    public final TableField<BountyRecord, String> TITLE = createField(DSL.name("title"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>public.bounty.acceptance_criteria</code>.
     */
    public final TableField<BountyRecord, String> ACCEPTANCE_CRITERIA = createField(DSL.name("acceptance_criteria"), SQLDataType.VARCHAR.nullable(false).defaultValue(DSL.field("''::character varying", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>public.bounty.problem_statement</code>.
     */
    public final TableField<BountyRecord, String> PROBLEM_STATEMENT = createField(DSL.name("problem_statement"), SQLDataType.VARCHAR.nullable(false).defaultValue(DSL.field("''::character varying", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>public.bounty.languages</code>.
     */
    public final TableField<BountyRecord, String[]> LANGUAGES = createField(DSL.name("languages"), SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.bounty.bounty_value</code>.
     */
    public final TableField<BountyRecord, BigDecimal> BOUNTY_VALUE = createField(DSL.name("bounty_value"), SQLDataType.NUMERIC.nullable(false), this, "");

    /**
     * The column <code>public.bounty.bounty_value_usd</code>.
     */
    public final TableField<BountyRecord, BigDecimal> BOUNTY_VALUE_USD = createField(DSL.name("bounty_value_usd"), SQLDataType.NUMERIC.nullable(false).defaultValue(DSL.field("0.0", SQLDataType.NUMERIC)), this, "");

    /**
     * The column <code>public.bounty.bounty_currency</code>.
     */
    public final TableField<BountyRecord, String> BOUNTY_CURRENCY = createField(DSL.name("bounty_currency"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>public.bounty.tags</code>.
     */
    public final TableField<BountyRecord, String[]> TAGS = createField(DSL.name("tags"), SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.bounty.experience</code>.
     */
    public final TableField<BountyRecord, String> EXPERIENCE = createField(DSL.name("experience"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>public.bounty.bounty_type</code>.
     */
    public final TableField<BountyRecord, String> BOUNTY_TYPE = createField(DSL.name("bounty_type"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>public.bounty.firebase_user_id</code>.
     */
    public final TableField<BountyRecord, String> FIREBASE_USER_ID = createField(DSL.name("firebase_user_id"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>public.bounty.issue_number</code>.
     */
    public final TableField<BountyRecord, Long> ISSUE_NUMBER = createField(DSL.name("issue_number"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.bounty.repo_owner</code>.
     */
    public final TableField<BountyRecord, String> REPO_OWNER = createField(DSL.name("repo_owner"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>public.bounty.repo_name</code>.
     */
    public final TableField<BountyRecord, String> REPO_NAME = createField(DSL.name("repo_name"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>public.bounty.bounty_status</code>.
     */
    public final TableField<BountyRecord, String> BOUNTY_STATUS = createField(DSL.name("bounty_status"), SQLDataType.VARCHAR.nullable(false).defaultValue(DSL.field("'PENDING'::character varying", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>public.bounty.completed_by</code>.
     */
    public final TableField<BountyRecord, String> COMPLETED_BY = createField(DSL.name("completed_by"), SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.bounty.completed_at</code>.
     */
    public final TableField<BountyRecord, LocalDateTime> COMPLETED_AT = createField(DSL.name("completed_at"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.bounty.created_at</code>.
     */
    public final TableField<BountyRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("now()", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>public.bounty.expires_at</code>.
     */
    public final TableField<BountyRecord, LocalDateTime> EXPIRES_AT = createField(DSL.name("expires_at"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("now()", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>public.bounty.blockchain_address</code>.
     */
    public final TableField<BountyRecord, String> BLOCKCHAIN_ADDRESS = createField(DSL.name("blockchain_address"), SQLDataType.VARCHAR, this, "");

    private Bounty(Name alias, Table<BountyRecord> aliased) {
        this(alias, aliased, null);
    }

    private Bounty(Name alias, Table<BountyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.bounty</code> table reference
     */
    public Bounty(String alias) {
        this(DSL.name(alias), BOUNTY);
    }

    /**
     * Create an aliased <code>public.bounty</code> table reference
     */
    public Bounty(Name alias) {
        this(alias, BOUNTY);
    }

    /**
     * Create a <code>public.bounty</code> table reference
     */
    public Bounty() {
        this(DSL.name("bounty"), null);
    }

    public <O extends Record> Bounty(Table<O> child, ForeignKey<O, BountyRecord> key) {
        super(child, key, BOUNTY);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public UniqueKey<BountyRecord> getPrimaryKey() {
        return Keys.BOUNTY_PKEY;
    }

    @Override
    public List<UniqueKey<BountyRecord>> getKeys() {
        return Arrays.<UniqueKey<BountyRecord>>asList(Keys.BOUNTY_PKEY);
    }

    @Override
    public Bounty as(String alias) {
        return new Bounty(DSL.name(alias), this);
    }

    @Override
    public Bounty as(Name alias) {
        return new Bounty(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Bounty rename(String name) {
        return new Bounty(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Bounty rename(Name name) {
        return new Bounty(name, null);
    }
}
