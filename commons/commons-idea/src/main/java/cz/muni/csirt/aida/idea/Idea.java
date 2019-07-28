
package cz.muni.csirt.aida.idea;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  = IDEA0 format definition =
 * 
 * Keys use !CamelCase, however to avoid confusion, they must be case insensitively unique within their parent object. When parsing, keys "ID", "id", "iD" and "Id" must be considered as equivalent.
 * 
 * Each definition line is in form KEY: TYPE, followed by an explanation line, where type can be basic JSON type (in ''italics''), syntactically restricted type (with reference to [[#Types|Types]] chapter), or array of former two (order is important). Types define expected syntax, however their content may be further syntactically or semantically restricted according to particular key explanation.
 * 
 * The keys ''Format'', ''ID'', ''!DetectTime'' and ''Category'' are mandatory, rest of the keys is optional (nonexistent key indicates that information is not applicable or unknown).
 * 
 * As human language may be ambiguous inadvertently or by omission, when in doubt, consult [[IDEA/Schema|JSON schema]].
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Format",
    "ID",
    "AltNames",
    "CorrelID",
    "AggrID",
    "PredID",
    "RelID",
    "CreateTime",
    "DetectTime",
    "EventTime",
    "CeaseTime",
    "WinStartTime",
    "WinEndTime",
    "ConnCount",
    "FlowCount",
    "PacketCount",
    "ByteCount",
    "Category",
    "Ref",
    "Confidence",
    "Description",
    "Note",
    "Source",
    "Target",
    "Attach",
    "Node"
})
public class Idea {

    /**
     * Must contain string "IDEA0". (Trailing zero denotes draft version, after review/discussion and specification finalisation the name will change.)
     * (Required)
     * 
     */
    @JsonProperty("Format")
    @JsonPropertyDescription("Must contain string \"IDEA0\". (Trailing zero denotes draft version, after review/discussion and specification finalisation the name will change.)")
    private Idea.Format format;
    /**
     * String, containing reasonably globally unique identifier. UUID version 4 (random) or 5 (SHA-1) is recommended. As IDs are meant to be used at other mediums, transfer protocols and formats (an example being query string fields in URL), they are allowed to contain only reasonably safe subset of characters. May thus contain only alphanumeric, dot, minus sign and underscore and must not be empty.
     * (Required)
     *
     */
    @JsonProperty("ID")
    @JsonPropertyDescription("String, containing reasonably globally unique identifier. UUID version 4 (random) or 5\u00a0(SHA-1) is recommended. As IDs are meant to be used at other mediums, transfer protocols and formats (an example being query string fields in URL), they are allowed to contain only reasonably safe subset of characters. May thus contain only alphanumeric, dot, minus sign and underscore and must not be empty.")
    private String iD;
    /**
     * Array of alternative identifiers.
     *
     */
    @JsonProperty("AltNames")
    @JsonPropertyDescription("Array of alternative identifiers.")
    private List<String> altNames = null;
    /**
     * Array of correlated messages identifiers.
     *
     */
    @JsonProperty("CorrelID")
    @JsonPropertyDescription("Array of correlated messages identifiers.")
    private List<String> correlID = null;
    /**
     * Array of aggregated messages identifiers.
     *
     */
    @JsonProperty("AggrID")
    @JsonPropertyDescription("Array of aggregated messages identifiers.")
    private List<String> aggrID = null;
    /**
     * Array of obsoleted messages identifiers.
     *
     */
    @JsonProperty("PredID")
    @JsonPropertyDescription("Array of obsoleted messages identifiers.")
    private List<String> predID = null;
    /**
     * Array of related messages identifiers.
     *
     */
    @JsonProperty("RelID")
    @JsonPropertyDescription("Array of related messages identifiers.")
    private List<String> relID = null;
    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("CreateTime")
    @JsonPropertyDescription("String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].")
    private Date createTime;
    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     * (Required)
     *
     */
    @JsonProperty("DetectTime")
    @JsonPropertyDescription("String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].")
    private Date detectTime;
    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("EventTime")
    @JsonPropertyDescription("String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].")
    private Date eventTime;
    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("CeaseTime")
    @JsonPropertyDescription("String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].")
    private Date ceaseTime;
    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("WinStartTime")
    @JsonPropertyDescription("String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].")
    private Date winStartTime;
    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("WinEndTime")
    @JsonPropertyDescription("String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].")
    private Date winEndTime;
    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("ConnCount")
    @JsonPropertyDescription("JSON \"number\" with no fractional and exponential part.")
    private Long connCount;
    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("FlowCount")
    @JsonPropertyDescription("JSON \"number\" with no fractional and exponential part.")
    private Long flowCount;
    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("PacketCount")
    @JsonPropertyDescription("JSON \"number\" with no fractional and exponential part.")
    private Long packetCount;
    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("ByteCount")
    @JsonPropertyDescription("JSON \"number\" with no fractional and exponential part.")
    private Long byteCount;
    /**
     * Array of event categories.
     * (Required)
     *
     */
    @JsonProperty("Category")
    @JsonPropertyDescription("Array of event categories.")
    private List<String> category = null;
    /**
     * Array of references.
     *
     */
    @JsonProperty("Ref")
    @JsonPropertyDescription("Array of references.")
    private List<URI> ref = null;
    /**
     * Confidence of detector in its own reliability of this particular detection. (0 – surely false, 1 – no doubts). If key is not presented, detector does not know (or has no capability to estimate the confidence).
     *
     */
    @JsonProperty("Confidence")
    @JsonPropertyDescription("Confidence of detector in its own reliability of this particular detection. (0 \u2013 surely false, 1 \u2013 no doubts). If key is not presented, detector does not know (or has no capability to estimate the confidence).")
    private Double confidence;
    /**
     * Short free text human readable description.
     *
     */
    @JsonProperty("Description")
    @JsonPropertyDescription("Short free text human readable description.")
    private String description;
    /**
     * Free text human readable addidional note, possibly longer description of incident if not obvious.
     *
     */
    @JsonProperty("Note")
    @JsonPropertyDescription("Free text human readable addidional note, possibly longer description of incident if not obvious.")
    private String note;
    /**
     * Array of source or target descriptions.
     *
     */
    @JsonProperty("Source")
    @JsonPropertyDescription("Array of source or target descriptions.")
    private List<Source> source = null;
    /**
     * Array of source or target descriptions.
     *
     */
    @JsonProperty("Target")
    @JsonPropertyDescription("Array of source or target descriptions.")
    private List<Target> target = null;
    /**
     * Array of attachment descriptions.
     *
     */
    @JsonProperty("Attach")
    @JsonPropertyDescription("Array of attachment descriptions.")
    private List<Attach> attach = null;
    /**
     * Array of detector descriptions.
     *
     */
    @JsonProperty("Node")
    @JsonPropertyDescription("Array of detector descriptions.")
    private List<Node> node = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Idea() {
    }

    /**
     *
     * @param createTime
     * @param packetCount
     * @param correlID
     * @param byteCount
     * @param description
     * @param winStartTime
     * @param note
     * @param flowCount
     * @param winEndTime
     * @param connCount
     * @param format
     * @param confidence
     * @param ceaseTime
     * @param aggrID
     * @param iD
     * @param altNames
     * @param ref
     * @param node
     * @param category
     * @param source
     * @param attach
     * @param predID
     * @param relID
     * @param target
     * @param eventTime
     * @param detectTime
     */
    public Idea(Idea.Format format, String iD, List<String> altNames, List<String> correlID, List<String> aggrID, List<String> predID, List<String> relID, Date createTime, Date detectTime, Date eventTime, Date ceaseTime, Date winStartTime, Date winEndTime, Long connCount, Long flowCount, Long packetCount, Long byteCount, List<String> category, List<URI> ref, Double confidence, String description, String note, List<Source> source, List<Target> target, List<Attach> attach, List<Node> node) {
        super();
        this.format = format;
        this.iD = iD;
        this.altNames = altNames;
        this.correlID = correlID;
        this.aggrID = aggrID;
        this.predID = predID;
        this.relID = relID;
        this.createTime = createTime;
        this.detectTime = detectTime;
        this.eventTime = eventTime;
        this.ceaseTime = ceaseTime;
        this.winStartTime = winStartTime;
        this.winEndTime = winEndTime;
        this.connCount = connCount;
        this.flowCount = flowCount;
        this.packetCount = packetCount;
        this.byteCount = byteCount;
        this.category = category;
        this.ref = ref;
        this.confidence = confidence;
        this.description = description;
        this.note = note;
        this.source = source;
        this.target = target;
        this.attach = attach;
        this.node = node;
    }

    /**
     * Must contain string "IDEA0". (Trailing zero denotes draft version, after review/discussion and specification finalisation the name will change.)
     * (Required)
     *
     */
    @JsonProperty("Format")
    public Idea.Format getFormat() {
        return format;
    }

    /**
     * Must contain string "IDEA0". (Trailing zero denotes draft version, after review/discussion and specification finalisation the name will change.)
     * (Required)
     *
     */
    @JsonProperty("Format")
    public void setFormat(Idea.Format format) {
        this.format = format;
    }

    /**
     * String, containing reasonably globally unique identifier. UUID version 4 (random) or 5 (SHA-1) is recommended. As IDs are meant to be used at other mediums, transfer protocols and formats (an example being query string fields in URL), they are allowed to contain only reasonably safe subset of characters. May thus contain only alphanumeric, dot, minus sign and underscore and must not be empty.
     * (Required)
     *
     */
    @JsonProperty("ID")
    public String getID() {
        return iD;
    }

    /**
     * String, containing reasonably globally unique identifier. UUID version 4 (random) or 5 (SHA-1) is recommended. As IDs are meant to be used at other mediums, transfer protocols and formats (an example being query string fields in URL), they are allowed to contain only reasonably safe subset of characters. May thus contain only alphanumeric, dot, minus sign and underscore and must not be empty.
     * (Required)
     *
     */
    @JsonProperty("ID")
    public void setID(String iD) {
        this.iD = iD;
    }

    /**
     * Array of alternative identifiers.
     *
     */
    @JsonProperty("AltNames")
    public List<String> getAltNames() {
        return altNames;
    }

    /**
     * Array of alternative identifiers.
     *
     */
    @JsonProperty("AltNames")
    public void setAltNames(List<String> altNames) {
        this.altNames = altNames;
    }

    /**
     * Array of correlated messages identifiers.
     *
     */
    @JsonProperty("CorrelID")
    public List<String> getCorrelID() {
        return correlID;
    }

    /**
     * Array of correlated messages identifiers.
     *
     */
    @JsonProperty("CorrelID")
    public void setCorrelID(List<String> correlID) {
        this.correlID = correlID;
    }

    /**
     * Array of aggregated messages identifiers.
     *
     */
    @JsonProperty("AggrID")
    public List<String> getAggrID() {
        return aggrID;
    }

    /**
     * Array of aggregated messages identifiers.
     *
     */
    @JsonProperty("AggrID")
    public void setAggrID(List<String> aggrID) {
        this.aggrID = aggrID;
    }

    /**
     * Array of obsoleted messages identifiers.
     *
     */
    @JsonProperty("PredID")
    public List<String> getPredID() {
        return predID;
    }

    /**
     * Array of obsoleted messages identifiers.
     *
     */
    @JsonProperty("PredID")
    public void setPredID(List<String> predID) {
        this.predID = predID;
    }

    /**
     * Array of related messages identifiers.
     *
     */
    @JsonProperty("RelID")
    public List<String> getRelID() {
        return relID;
    }

    /**
     * Array of related messages identifiers.
     *
     */
    @JsonProperty("RelID")
    public void setRelID(List<String> relID) {
        this.relID = relID;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("CreateTime")
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("CreateTime")
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     * (Required)
     *
     */
    @JsonProperty("DetectTime")
    public Date getDetectTime() {
        return detectTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     * (Required)
     *
     */
    @JsonProperty("DetectTime")
    public void setDetectTime(Date detectTime) {
        this.detectTime = detectTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("EventTime")
    public Date getEventTime() {
        return eventTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("EventTime")
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("CeaseTime")
    public Date getCeaseTime() {
        return ceaseTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("CeaseTime")
    public void setCeaseTime(Date ceaseTime) {
        this.ceaseTime = ceaseTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("WinStartTime")
    public Date getWinStartTime() {
        return winStartTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("WinStartTime")
    public void setWinStartTime(Date winStartTime) {
        this.winStartTime = winStartTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("WinEndTime")
    public Date getWinEndTime() {
        return winEndTime;
    }

    /**
     * String, containing timestamp conforming to [[http://tools.ietf.org/html/rfc3339|RFC 3339]].
     *
     */
    @JsonProperty("WinEndTime")
    public void setWinEndTime(Date winEndTime) {
        this.winEndTime = winEndTime;
    }

    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("ConnCount")
    public Long getConnCount() {
        return connCount;
    }

    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("ConnCount")
    public void setConnCount(Long connCount) {
        this.connCount = connCount;
    }

    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("FlowCount")
    public Long getFlowCount() {
        return flowCount;
    }

    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("FlowCount")
    public void setFlowCount(Long flowCount) {
        this.flowCount = flowCount;
    }

    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("PacketCount")
    public Long getPacketCount() {
        return packetCount;
    }

    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("PacketCount")
    public void setPacketCount(Long packetCount) {
        this.packetCount = packetCount;
    }

    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("ByteCount")
    public Long getByteCount() {
        return byteCount;
    }

    /**
     * JSON "number" with no fractional and exponential part.
     *
     */
    @JsonProperty("ByteCount")
    public void setByteCount(Long byteCount) {
        this.byteCount = byteCount;
    }

    /**
     * Array of event categories.
     * (Required)
     *
     */
    @JsonProperty("Category")
    public List<String> getCategory() {
        return category;
    }

    /**
     * Array of event categories.
     * (Required)
     *
     */
    @JsonProperty("Category")
    public void setCategory(List<String> category) {
        this.category = category;
    }

    /**
     * Array of references.
     *
     */
    @JsonProperty("Ref")
    public List<URI> getRef() {
        return ref;
    }

    /**
     * Array of references.
     *
     */
    @JsonProperty("Ref")
    public void setRef(List<URI> ref) {
        this.ref = ref;
    }

    /**
     * Confidence of detector in its own reliability of this particular detection. (0 – surely false, 1 – no doubts). If key is not presented, detector does not know (or has no capability to estimate the confidence).
     *
     */
    @JsonProperty("Confidence")
    public Double getConfidence() {
        return confidence;
    }

    /**
     * Confidence of detector in its own reliability of this particular detection. (0 – surely false, 1 – no doubts). If key is not presented, detector does not know (or has no capability to estimate the confidence).
     *
     */
    @JsonProperty("Confidence")
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    /**
     * Short free text human readable description.
     *
     */
    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    /**
     * Short free text human readable description.
     *
     */
    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Free text human readable addidional note, possibly longer description of incident if not obvious.
     *
     */
    @JsonProperty("Note")
    public String getNote() {
        return note;
    }

    /**
     * Free text human readable addidional note, possibly longer description of incident if not obvious.
     *
     */
    @JsonProperty("Note")
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Array of source or target descriptions.
     *
     */
    @JsonProperty("Source")
    public List<Source> getSource() {
        return source;
    }

    /**
     * Array of source or target descriptions.
     *
     */
    @JsonProperty("Source")
    public void setSource(List<Source> source) {
        this.source = source;
    }

    /**
     * Array of source or target descriptions.
     *
     */
    @JsonProperty("Target")
    public List<Target> getTarget() {
        return target;
    }

    /**
     * Array of source or target descriptions.
     *
     */
    @JsonProperty("Target")
    public void setTarget(List<Target> target) {
        this.target = target;
    }

    /**
     * Array of attachment descriptions.
     *
     */
    @JsonProperty("Attach")
    public List<Attach> getAttach() {
        return attach;
    }

    /**
     * Array of attachment descriptions.
     *
     */
    @JsonProperty("Attach")
    public void setAttach(List<Attach> attach) {
        this.attach = attach;
    }

    /**
     * Array of detector descriptions.
     *
     */
    @JsonProperty("Node")
    public List<Node> getNode() {
        return node;
    }

    /**
     * Array of detector descriptions.
     *
     */
    @JsonProperty("Node")
    public void setNode(List<Node> node) {
        this.node = node;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Format {

        IDEA_0("IDEA0");
        private final String value;
        private final static Map<String, Format> CONSTANTS = new HashMap<String, Format>();

        static {
            for (Idea.Format c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Format(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Idea.Format fromValue(String value) {
            Idea.Format constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
