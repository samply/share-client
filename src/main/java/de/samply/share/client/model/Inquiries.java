package de.samply.share.client.model;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * A collection of inquiry ids and revisions as read from a searchbroker
 */
@Root (name = "Inquiries", strict = false)
public class Inquiries {

    /** The inquiries. */
    @ElementList(entry = "Inquiry", inline = true, required = false)
    private List<Inquiry> inquiries;

    /**
     * Gets the inquiries.
     *
     * @return the inquiries
     */
    public List<Inquiry> getInquiries() {
        return inquiries;
    }

    /**
     * The Class Inquiry.
     */
    @Root (name = "Inquiry")
    public static class Inquiry {

        /** The id. */
        @Element(name = "Id")
        private String id;

        /** The revision. */
        @Element(name = "Revision")
        private String revision;

        /**
         * Gets the id.
         *
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * Gets the revision.
         *
         * @return the revision
         */
        public String getRevision() {
            return revision;
        }

    }
}