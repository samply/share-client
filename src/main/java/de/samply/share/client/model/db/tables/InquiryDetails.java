/*
 * This file is generated by jOOQ.
*/
package de.samply.share.client.model.db.tables;


import de.samply.share.client.model.db.Keys;
import de.samply.share.client.model.db.Samply;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.records.InquiryDetailsRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class InquiryDetails extends TableImpl<InquiryDetailsRecord> {

    private static final long serialVersionUID = -2027256272;

    /**
     * The reference instance of <code>samply.inquiry_details</code>
     */
    public static final InquiryDetails INQUIRY_DETAILS = new InquiryDetails();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InquiryDetailsRecord> getRecordType() {
        return InquiryDetailsRecord.class;
    }

    /**
     * The column <code>samply.inquiry_details.id</code>.
     */
    public final TableField<InquiryDetailsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('samply.inquiry_details_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>samply.inquiry_details.inquiry_id</code>.
     */
    public final TableField<InquiryDetailsRecord, Integer> INQUIRY_ID = createField("inquiry_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>samply.inquiry_details.contact_id</code>.
     */
    public final TableField<InquiryDetailsRecord, Integer> CONTACT_ID = createField("contact_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>samply.inquiry_details.revision</code>.
     */
    public final TableField<InquiryDetailsRecord, Integer> REVISION = createField("revision", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>samply.inquiry_details.received_at</code>.
     */
    public final TableField<InquiryDetailsRecord, Timestamp> RECEIVED_AT = createField("received_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>samply.inquiry_details.status</code>.
     */
    public final TableField<InquiryDetailsRecord, InquiryStatusType> STATUS = createField("status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(de.samply.share.client.model.db.enums.InquiryStatusType.class), this, "");

    /**
     * The column <code>samply.inquiry_details.criteria_original</code>. the criteria xml snippet as received from source
     */
    public final TableField<InquiryDetailsRecord, String> CRITERIA_ORIGINAL = createField("criteria_original", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "the criteria xml snippet as received from source");

    /**
     * The column <code>samply.inquiry_details.criteria_modified</code>. if the original criteria had unknown keys which were removed...keep the modified one as well
     */
    public final TableField<InquiryDetailsRecord, String> CRITERIA_MODIFIED = createField("criteria_modified", org.jooq.impl.SQLDataType.CLOB, this, "if the original criteria had unknown keys which were removed...keep the modified one as well");

    /**
     * The column <code>samply.inquiry_details.expose_location</code>.
     */
    public final TableField<InquiryDetailsRecord, String> EXPOSE_LOCATION = createField("expose_location", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>samply.inquiry_details</code> table reference
     */
    public InquiryDetails() {
        this("inquiry_details", null);
    }

    /**
     * Create an aliased <code>samply.inquiry_details</code> table reference
     */
    public InquiryDetails(String alias) {
        this(alias, INQUIRY_DETAILS);
    }

    private InquiryDetails(String alias, Table<InquiryDetailsRecord> aliased) {
        this(alias, aliased, null);
    }

    private InquiryDetails(String alias, Table<InquiryDetailsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Samply.SAMPLY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<InquiryDetailsRecord, Integer> getIdentity() {
        return Keys.IDENTITY_INQUIRY_DETAILS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<InquiryDetailsRecord> getPrimaryKey() {
        return Keys.INQUIRY_DETAILS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<InquiryDetailsRecord>> getKeys() {
        return Arrays.<UniqueKey<InquiryDetailsRecord>>asList(Keys.INQUIRY_DETAILS_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<InquiryDetailsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<InquiryDetailsRecord, ?>>asList(Keys.INQUIRY_DETAILS__INQUIRY_DETAILS_INQUIRY_ID_FKEY, Keys.INQUIRY_DETAILS__INQUIRY_DETAILS_CONTACT_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InquiryDetails as(String alias) {
        return new InquiryDetails(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public InquiryDetails rename(String name) {
        return new InquiryDetails(name, null);
    }
}
