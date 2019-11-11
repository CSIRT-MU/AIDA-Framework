package cz.muni.csirt.aida.mining.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for working with {@link Rule} objects.
 */
public class Rules {

    private Rules() {
    }

    /**
     * Parse rule from the SPMF like format: "{item} [,{item}]... ==> {item} [,{item}]..."
     * where item is defined as {@link Item} ("{category}_{node}_{port}").
     *
     * See {@link #toSpmf(Rule)} for reverse operation.
     *
     * Example:
     *      cz.cesnet.tarpit_Recon.Scanning_2323,cz.cesnet.nemea.hoststats_Recon.Scanning_None ==> cz.cesnet.tarpit_Recon.Scanning_23
     *
     * @param rule String representing the rule in the SPMF like format
     * @param support Support of the rule
     * @param confidence Confidence of the rule
     * @return Rule object representation of given rule.
     */
    public static Rule fromSpmf(String rule, int support, double confidence) {
        String[] parts = rule.trim().split("==>", 2);
        Set<Item> antecedent = parseItemSet(parts[0]);
        Set<Item> consequent = parseItemSet(parts[1]);

        return new Rule(antecedent, consequent, support, confidence);
    }

    /**
     * Format rule as a string in SPMF like format. See {@link #fromSpmf(String, int, double)} for more information.
     * @param rule rule
     * @return String representation of the rule.
     */
    public static String toSpmf(Rule rule) {
        return String.join(" ==> ",
                Rules.joinItemSet(rule.getAntecedent()),
                Rules.joinItemSet(rule.getConsequent()));
    }

    private static Set<Item> parseItemSet(String events) {
        return Arrays.stream(events.trim().split(","))
                .map(String::trim)
                .map(Item::fromString)
                .collect(Collectors.toSet());
    }

    private static String joinItemSet(Set<Item> items) {
        return items.stream()
                .map(Item::toString)
                .collect(Collectors.joining(", "));
    }

}
