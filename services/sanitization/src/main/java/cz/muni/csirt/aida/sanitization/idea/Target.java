
package cz.muni.csirt.aida.sanitization.idea;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Information concerning particular source or target.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Type",
    "Hostname",
    "IP4",
    "MAC",
    "IP6",
    "Port",
    "Proto",
    "URL",
    "Email",
    "AttachHand",
    "Note",
    "Spoofed",
    "Imprecise",
    "Anonymised",
    "ASN",
    "Router",
    "Netname",
    "Ref"
})
public class Target {

    /**
     * Array of source/target categories.
     * 
     */
    @JsonProperty("Type")
    @JsonPropertyDescription("Array of source/target categories.")
    private List<String> type = null;
    /**
     * Array of hostnames.
     * 
     */
    @JsonProperty("Hostname")
    @JsonPropertyDescription("Array of hostnames.")
    private List<String> hostname = null;
    /**
     * Array of IPv4 addresses.
     * 
     */
    @JsonProperty("IP4")
    @JsonPropertyDescription("Array of IPv4 addresses.")
    private List<String> iP4 = null;
    /**
     * Array of MAC addresses.
     * 
     */
    @JsonProperty("MAC")
    @JsonPropertyDescription("Array of MAC addresses.")
    private List<String> mAC = null;
    /**
     * Array of IPv6 addresses.
     * 
     */
    @JsonProperty("IP6")
    @JsonPropertyDescription("Array of IPv6 addresses.")
    private List<String> iP6 = null;
    /**
     * Array of port numbers.
     * 
     */
    @JsonProperty("Port")
    @JsonPropertyDescription("Array of port numbers.")
    private List<Integer> port = null;
    /**
     * Array of protocol names.
     * 
     */
    @JsonProperty("Proto")
    @JsonPropertyDescription("Array of protocol names.")
    private List<String> proto = null;
    /**
     * Array of URLs.
     * 
     */
    @JsonProperty("URL")
    @JsonPropertyDescription("Array of URLs.")
    private List<String> uRL = null;
    /**
     * Array of email addresses.
     * 
     */
    @JsonProperty("Email")
    @JsonPropertyDescription("Array of email addresses.")
    private List<String> email = null;
    /**
     * Array of attachment identifiers.
     * 
     */
    @JsonProperty("AttachHand")
    @JsonPropertyDescription("Array of attachment identifiers.")
    private List<String> attachHand = null;
    /**
     * Free text human readable additional note.
     * 
     */
    @JsonProperty("Note")
    @JsonPropertyDescription("Free text human readable additional note.")
    private String note;
    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Spoofed")
    @JsonPropertyDescription("JSON \"true\" or \"false\" value.")
    private Boolean spoofed;
    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Imprecise")
    @JsonPropertyDescription("JSON \"true\" or \"false\" value.")
    private Boolean imprecise;
    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Anonymised")
    @JsonPropertyDescription("JSON \"true\" or \"false\" value.")
    private Boolean anonymised;
    /**
     * Autonomous system numbers.
     * 
     */
    @JsonProperty("ASN")
    @JsonPropertyDescription("Autonomous system numbers.")
    private List<Integer> aSN = null;
    /**
     * Array of router/interface paths.
     * 
     */
    @JsonProperty("Router")
    @JsonPropertyDescription("Array of router/interface paths.")
    private List<String> router = null;
    /**
     * Array of RIR network identifiers.
     * 
     */
    @JsonProperty("Netname")
    @JsonPropertyDescription("Array of RIR network identifiers.")
    private List<URI> netname = null;
    /**
     * Array of references.
     * 
     */
    @JsonProperty("Ref")
    @JsonPropertyDescription("Array of references.")
    private List<URI> ref = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Target() {
    }

    /**
     * 
     * @param port
     * @param aSN
     * @param netname
     * @param mAC
     * @param uRL
     * @param hostname
     * @param attachHand
     * @param type
     * @param proto
     * @param ref
     * @param imprecise
     * @param email
     * @param router
     * @param anonymised
     * @param spoofed
     * @param iP4
     * @param note
     * @param iP6
     */
    public Target(List<String> type, List<String> hostname, List<String> iP4, List<String> mAC, List<String> iP6, List<Integer> port, List<String> proto, List<String> uRL, List<String> email, List<String> attachHand, String note, Boolean spoofed, Boolean imprecise, Boolean anonymised, List<Integer> aSN, List<String> router, List<URI> netname, List<URI> ref) {
        super();
        this.type = type;
        this.hostname = hostname;
        this.iP4 = iP4;
        this.mAC = mAC;
        this.iP6 = iP6;
        this.port = port;
        this.proto = proto;
        this.uRL = uRL;
        this.email = email;
        this.attachHand = attachHand;
        this.note = note;
        this.spoofed = spoofed;
        this.imprecise = imprecise;
        this.anonymised = anonymised;
        this.aSN = aSN;
        this.router = router;
        this.netname = netname;
        this.ref = ref;
    }

    /**
     * Array of source/target categories.
     * 
     */
    @JsonProperty("Type")
    public List<String> getType() {
        return type;
    }

    /**
     * Array of source/target categories.
     * 
     */
    @JsonProperty("Type")
    public void setType(List<String> type) {
        this.type = type;
    }

    /**
     * Array of hostnames.
     * 
     */
    @JsonProperty("Hostname")
    public List<String> getHostname() {
        return hostname;
    }

    /**
     * Array of hostnames.
     * 
     */
    @JsonProperty("Hostname")
    public void setHostname(List<String> hostname) {
        this.hostname = hostname;
    }

    /**
     * Array of IPv4 addresses.
     * 
     */
    @JsonProperty("IP4")
    public List<String> getIP4() {
        return iP4;
    }

    /**
     * Array of IPv4 addresses.
     * 
     */
    @JsonProperty("IP4")
    public void setIP4(List<String> iP4) {
        this.iP4 = iP4;
    }

    /**
     * Array of MAC addresses.
     * 
     */
    @JsonProperty("MAC")
    public List<String> getMAC() {
        return mAC;
    }

    /**
     * Array of MAC addresses.
     * 
     */
    @JsonProperty("MAC")
    public void setMAC(List<String> mAC) {
        this.mAC = mAC;
    }

    /**
     * Array of IPv6 addresses.
     * 
     */
    @JsonProperty("IP6")
    public List<String> getIP6() {
        return iP6;
    }

    /**
     * Array of IPv6 addresses.
     * 
     */
    @JsonProperty("IP6")
    public void setIP6(List<String> iP6) {
        this.iP6 = iP6;
    }

    /**
     * Array of port numbers.
     * 
     */
    @JsonProperty("Port")
    public List<Integer> getPort() {
        return port;
    }

    /**
     * Array of port numbers.
     * 
     */
    @JsonProperty("Port")
    public void setPort(List<Integer> port) {
        this.port = port;
    }

    /**
     * Array of protocol names.
     * 
     */
    @JsonProperty("Proto")
    public List<String> getProto() {
        return proto;
    }

    /**
     * Array of protocol names.
     * 
     */
    @JsonProperty("Proto")
    public void setProto(List<String> proto) {
        this.proto = proto;
    }

    /**
     * Array of URLs.
     * 
     */
    @JsonProperty("URL")
    public List<String> getURL() {
        return uRL;
    }

    /**
     * Array of URLs.
     * 
     */
    @JsonProperty("URL")
    public void setURL(List<String> uRL) {
        this.uRL = uRL;
    }

    /**
     * Array of email addresses.
     * 
     */
    @JsonProperty("Email")
    public List<String> getEmail() {
        return email;
    }

    /**
     * Array of email addresses.
     * 
     */
    @JsonProperty("Email")
    public void setEmail(List<String> email) {
        this.email = email;
    }

    /**
     * Array of attachment identifiers.
     * 
     */
    @JsonProperty("AttachHand")
    public List<String> getAttachHand() {
        return attachHand;
    }

    /**
     * Array of attachment identifiers.
     * 
     */
    @JsonProperty("AttachHand")
    public void setAttachHand(List<String> attachHand) {
        this.attachHand = attachHand;
    }

    /**
     * Free text human readable additional note.
     * 
     */
    @JsonProperty("Note")
    public String getNote() {
        return note;
    }

    /**
     * Free text human readable additional note.
     * 
     */
    @JsonProperty("Note")
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Spoofed")
    public Boolean getSpoofed() {
        return spoofed;
    }

    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Spoofed")
    public void setSpoofed(Boolean spoofed) {
        this.spoofed = spoofed;
    }

    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Imprecise")
    public Boolean getImprecise() {
        return imprecise;
    }

    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Imprecise")
    public void setImprecise(Boolean imprecise) {
        this.imprecise = imprecise;
    }

    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Anonymised")
    public Boolean getAnonymised() {
        return anonymised;
    }

    /**
     * JSON "true" or "false" value.
     * 
     */
    @JsonProperty("Anonymised")
    public void setAnonymised(Boolean anonymised) {
        this.anonymised = anonymised;
    }

    /**
     * Autonomous system numbers.
     * 
     */
    @JsonProperty("ASN")
    public List<Integer> getASN() {
        return aSN;
    }

    /**
     * Autonomous system numbers.
     * 
     */
    @JsonProperty("ASN")
    public void setASN(List<Integer> aSN) {
        this.aSN = aSN;
    }

    /**
     * Array of router/interface paths.
     * 
     */
    @JsonProperty("Router")
    public List<String> getRouter() {
        return router;
    }

    /**
     * Array of router/interface paths.
     * 
     */
    @JsonProperty("Router")
    public void setRouter(List<String> router) {
        this.router = router;
    }

    /**
     * Array of RIR network identifiers.
     * 
     */
    @JsonProperty("Netname")
    public List<URI> getNetname() {
        return netname;
    }

    /**
     * Array of RIR network identifiers.
     * 
     */
    @JsonProperty("Netname")
    public void setNetname(List<URI> netname) {
        this.netname = netname;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
