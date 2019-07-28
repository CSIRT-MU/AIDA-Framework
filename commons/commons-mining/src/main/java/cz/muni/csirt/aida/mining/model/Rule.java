package cz.muni.csirt.aida.mining.model;

import java.util.Collections;
import java.util.Set;

public class Rule {

    private Set<Item> antecedent;
    private Set<Item> consequent;
    private int support;
    private double confidence;

    public Rule(Set<Item> antecedent, Set<Item> consequent, int support, double confidence) {
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.support = support;
        this.confidence = confidence;
    }

    public Set<Item> getAntecedent() {
        return Collections.unmodifiableSet(antecedent);
    }

    public Set<Item> getConsequent() {
        return Collections.unmodifiableSet(consequent);
    }

    public double getConfidence() {
        return confidence;
    }

    public int getSupport() {
        return support;
    }
}
