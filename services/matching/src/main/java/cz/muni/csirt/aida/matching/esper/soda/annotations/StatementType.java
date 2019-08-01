package cz.muni.csirt.aida.matching.esper.soda.annotations;

public enum StatementType {

    /**
     * Statement matching just preconditions of a rule.
     */
    PREDICTION,

    /**
     * Statement matching whole rule.
     */
    OBSERVATION,
}
