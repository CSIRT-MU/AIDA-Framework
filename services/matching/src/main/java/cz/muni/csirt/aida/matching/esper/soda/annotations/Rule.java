package cz.muni.csirt.aida.matching.esper.soda.annotations;


/**
 * Annotation designed to be used on statements representing rules.
 * This annotation maintains string representation of a rule.
 */
public @interface Rule {

    /**
     * Mandatory parameter
     * @return Returns string representation of the rule.
     */
    String value();

    /**
     * Rules confidence.
     * @return double
     */
    double confidence() default 0;

    /**
     * Type of the statement.
     * @return StatementType
     */
    StatementType type();
}
