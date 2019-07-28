
package cz.muni.csirt.aida.idea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Detector or possible intermediary (event aggregator, correlator, etc.) description.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Name",
    "Type",
    "SW",
    "AggrWin",
    "Note"
})
public class Node {

    /**
     * Namespaced identifier. Dot separated list of labels, with significance from left to right – leftmost denoting largest containing realm, rightmost denoting single entity. Country – organisation – suborganizations – machine – local scheme akin to "org.example.csirt.northwest.honeypot.jabberwock" is strongly recommended. Label case is insignificant, label can contain only letters, numbers or underscore and must not start with number.
     * 
     */
    @JsonProperty("Name")
    @JsonPropertyDescription("Namespaced identifier. Dot separated list of labels, with significance from left to right \u2013 leftmost denoting largest containing realm, rightmost denoting single entity. Country \u2013 organisation \u2013 suborganizations \u2013 machine \u2013 local scheme akin to \"org.example.csirt.northwest.honeypot.jabberwock\" is strongly recommended. Label case is insignificant, label can contain only letters, numbers or underscore and must not start with number.")
    private String name;
    /**
     * Array of detection node types.
     * 
     */
    @JsonProperty("Type")
    @JsonPropertyDescription("Array of detection node types.")
    private List<String> type = null;
    /**
     * Array of detection software names.
     * 
     */
    @JsonProperty("SW")
    @JsonPropertyDescription("Array of detection software names.")
    private List<String> sW = null;
    /**
     * String, containing time offset, intended for representing difference between two timestamps. Format is time part of [[http://tools.ietf.org/html/rfc3339|RFC 3339]], optionally prepended by "D" or "d" separator and number of days (which can have arbitrary number number of digits). "D" separator has been chosen to distinguish from internet time, and as a memory aid for "duration" or "days". For example "536D10:20:30.5" means 536 days, 10 hours, 20 seconds, 30.5 seconds, whereas 00:05:00 represents five minutes.
     * 
     * [[http://tools.ietf.org/html/rfc2234|ABNF]] syntax:
     * {{{
     * time-hour       = 2DIGIT  ; 00-23
     * time-minute     = 2DIGIT  ; 00-59
     * time-second     = 2DIGIT  ; 00-59
     * time-secfrac    = "." 1*DIGIT
     * separator       = "D" / "d"
     * days            = 1*DIGIT
     * 
     * duration        = [days separator] time-hour ":" time-minute ":" time-second [time-secfrac]
     * }}}
     * 
     */
    @JsonProperty("AggrWin")
    @JsonPropertyDescription("String, containing time offset, intended for representing difference between two timestamps. Format is time part of [[http://tools.ietf.org/html/rfc3339|RFC 3339]], optionally prepended by \"D\" or \"d\" separator and number of days (which can have arbitrary number number of digits). \"D\" separator has been chosen to distinguish from internet time, and as a memory aid for \"duration\" or \"days\". For example \"536D10:20:30.5\" means 536 days, 10 hours, 20 seconds, 30.5 seconds, whereas 00:05:00 represents five minutes.\n\n[[http://tools.ietf.org/html/rfc2234|ABNF]] syntax:\n{{{\ntime-hour       = 2DIGIT  ; 00-23\ntime-minute     = 2DIGIT  ; 00-59\ntime-second     = 2DIGIT  ; 00-59\ntime-secfrac    = \".\" 1*DIGIT\nseparator       = \"D\" / \"d\"\ndays            = 1*DIGIT\n\nduration        = [days separator] time-hour \":\" time-minute \":\" time-second [time-secfrac]\n}}}")
    private String aggrWin;
    /**
     * Free text human readable additional description.
     * 
     */
    @JsonProperty("Note")
    @JsonPropertyDescription("Free text human readable additional description.")
    private String note;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Node() {
    }

    /**
     * 
     * @param aggrWin
     * @param name
     * @param sW
     * @param type
     * @param note
     */
    public Node(String name, List<String> type, List<String> sW, String aggrWin, String note) {
        super();
        this.name = name;
        this.type = type;
        this.sW = sW;
        this.aggrWin = aggrWin;
        this.note = note;
    }

    /**
     * Namespaced identifier. Dot separated list of labels, with significance from left to right – leftmost denoting largest containing realm, rightmost denoting single entity. Country – organisation – suborganizations – machine – local scheme akin to "org.example.csirt.northwest.honeypot.jabberwock" is strongly recommended. Label case is insignificant, label can contain only letters, numbers or underscore and must not start with number.
     * 
     */
    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    /**
     * Namespaced identifier. Dot separated list of labels, with significance from left to right – leftmost denoting largest containing realm, rightmost denoting single entity. Country – organisation – suborganizations – machine – local scheme akin to "org.example.csirt.northwest.honeypot.jabberwock" is strongly recommended. Label case is insignificant, label can contain only letters, numbers or underscore and must not start with number.
     * 
     */
    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Array of detection node types.
     * 
     */
    @JsonProperty("Type")
    public List<String> getType() {
        return type;
    }

    /**
     * Array of detection node types.
     * 
     */
    @JsonProperty("Type")
    public void setType(List<String> type) {
        this.type = type;
    }

    /**
     * Array of detection software names.
     * 
     */
    @JsonProperty("SW")
    public List<String> getSW() {
        return sW;
    }

    /**
     * Array of detection software names.
     * 
     */
    @JsonProperty("SW")
    public void setSW(List<String> sW) {
        this.sW = sW;
    }

    /**
     * String, containing time offset, intended for representing difference between two timestamps. Format is time part of [[http://tools.ietf.org/html/rfc3339|RFC 3339]], optionally prepended by "D" or "d" separator and number of days (which can have arbitrary number number of digits). "D" separator has been chosen to distinguish from internet time, and as a memory aid for "duration" or "days". For example "536D10:20:30.5" means 536 days, 10 hours, 20 seconds, 30.5 seconds, whereas 00:05:00 represents five minutes.
     * 
     * [[http://tools.ietf.org/html/rfc2234|ABNF]] syntax:
     * {{{
     * time-hour       = 2DIGIT  ; 00-23
     * time-minute     = 2DIGIT  ; 00-59
     * time-second     = 2DIGIT  ; 00-59
     * time-secfrac    = "." 1*DIGIT
     * separator       = "D" / "d"
     * days            = 1*DIGIT
     * 
     * duration        = [days separator] time-hour ":" time-minute ":" time-second [time-secfrac]
     * }}}
     * 
     */
    @JsonProperty("AggrWin")
    public String getAggrWin() {
        return aggrWin;
    }

    /**
     * String, containing time offset, intended for representing difference between two timestamps. Format is time part of [[http://tools.ietf.org/html/rfc3339|RFC 3339]], optionally prepended by "D" or "d" separator and number of days (which can have arbitrary number number of digits). "D" separator has been chosen to distinguish from internet time, and as a memory aid for "duration" or "days". For example "536D10:20:30.5" means 536 days, 10 hours, 20 seconds, 30.5 seconds, whereas 00:05:00 represents five minutes.
     * 
     * [[http://tools.ietf.org/html/rfc2234|ABNF]] syntax:
     * {{{
     * time-hour       = 2DIGIT  ; 00-23
     * time-minute     = 2DIGIT  ; 00-59
     * time-second     = 2DIGIT  ; 00-59
     * time-secfrac    = "." 1*DIGIT
     * separator       = "D" / "d"
     * days            = 1*DIGIT
     * 
     * duration        = [days separator] time-hour ":" time-minute ":" time-second [time-secfrac]
     * }}}
     * 
     */
    @JsonProperty("AggrWin")
    public void setAggrWin(String aggrWin) {
        this.aggrWin = aggrWin;
    }

    /**
     * Free text human readable additional description.
     * 
     */
    @JsonProperty("Note")
    public String getNote() {
        return note;
    }

    /**
     * Free text human readable additional description.
     * 
     */
    @JsonProperty("Note")
    public void setNote(String note) {
        this.note = note;
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
