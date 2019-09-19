package cz.muni.csirt.aida.matching.esper.soda;

import com.espertech.esper.common.client.soda.*;

import cz.muni.csirt.aida.matching.esper.soda.annotations.StatementType;
import cz.muni.csirt.aida.mining.model.Item;
import cz.muni.csirt.aida.mining.model.Rule;
import cz.muni.csirt.aida.mining.model.Rules;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;


public class RuleStatements {

    private RuleStatements() {
    }

    /**
     * Match just the first part of a rule - preconditions.
     * @param rule rule
     * @return statement matching preconditions of given rule.
     */
    public static EPStatementObjectModel prediction(Rule rule) {
        return matchRecognize(rule, StatementType.PREDICTION);
    }

    /**
     * Match whole rule (preconditions and prediction as well).
     * @param rule rule to be matched
     * @return statement matching given rule.
     */
    public static EPStatementObjectModel observation(Rule rule) {
        return matchRecognize(rule, StatementType.OBSERVATION);
    }

    /**
     * Create Match Recognize statement for the given Rule.
     * @param rule rule
     * @param type statement type
     * @return statement
     */
    private static EPStatementObjectModel matchRecognize(Rule rule, StatementType type) {

        MatchRecognizeClause matchRecognize = new MatchRecognizeClause();

//         partition by source[0].IP4[0], target[0].IP4[0]
//        matchRecognize.setPartitionExpressions(
//                Arrays.asList(IdeaExpressions.sourceFirstIPv4(), IdeaExpressions.targetFirstIPv4()));

        // partition by source[0].IP4[0]

        matchRecognize.setPartitionExpressions(Collections.singletonList(IdeaExpressions.sourceFirstIPv4()));

        // Fill 'pattern', 'defines' and 'measures' with events

        MatchRecognizeRegExConcatenation pattern = new MatchRecognizeRegExConcatenation();
        matchRecognize.setPattern(pattern);

        Alphabet varNames = new Alphabet();

        MatchRecognizeRegExPermutation preconditions = new MatchRecognizeRegExPermutation();
        pattern.getChildren().add(preconditions);
        for (Item event : rule.getAntecedent()) {
            String ideaVar = varNames.next();

            // Add event into pattern
            preconditions.getChildren().add(
                    new MatchRecognizeRegExAtom(ideaVar, MatchRecogizePatternElementType.ONE_TO_MANY));

            // Add event into measures
            matchRecognize.getMeasures().add(new SelectClauseExpression(Expressions.property(ideaVar+"[0]"), ideaVar));

            // Add event into defines
            matchRecognize.getDefines().add(
                    new MatchRecognizeDefine(ideaVar, IdeaExpressions.eventConditions(event, ideaVar)));
        }

        if (type == StatementType.OBSERVATION) {
            MatchRecognizeRegExPermutation consequences = new MatchRecognizeRegExPermutation();

            MatchRecognizeRegEx anyEvent = new MatchRecognizeRegExAtom("ANY",
                    MatchRecogizePatternElementType.ZERO_TO_MANY_RELUCTANT);
            preconditions.getChildren().add(anyEvent);
            consequences.getChildren().add(anyEvent);

            pattern.getChildren().add(consequences);
            for (Item event : rule.getConsequent()) {
                String ideaVar = varNames.next();

                // Add event into pattern
                consequences.getChildren().add(
                        new MatchRecognizeRegExAtom(ideaVar, MatchRecogizePatternElementType.ONE_TO_MANY));

                // Add event into measures
                matchRecognize.getMeasures().add(new SelectClauseExpression(Expressions.property(ideaVar+"[0]"), ideaVar));

                // Add event into defines
                matchRecognize.getDefines().add(
                        new MatchRecognizeDefine(ideaVar, IdeaExpressions.eventConditions(event, ideaVar)));
            }
        }

        // Complete statement model

        EPStatementObjectModel model = new EPStatementObjectModel();
        model.selectClause(SelectClause.createWildcard());
        model.setMatchRecognizeClause(matchRecognize);

        model.fromClause(IdeaWindows.fromIdeaWindowFiltered(rule.getAntecedent()));
        if (type == StatementType.OBSERVATION) {
            Set<Item> allEvents = new HashSet<>();
            allEvents.addAll(rule.getAntecedent());
            allEvents.addAll(rule.getConsequent());
            model.fromClause(IdeaWindows.fromIdeaWindowFiltered(allEvents));
        }

        // Add annotations

        AnnotationPart ruleAnnotation = new AnnotationPart(
                "Rule",
                Arrays.asList(
                        new AnnotationAttribute("value", Rules.toSpmf(rule)),
                        new AnnotationAttribute("confidence", rule.getConfidence()),
                        new AnnotationAttribute("type", type))
                );
        model.setAnnotations(Collections.singletonList(ruleAnnotation));


        return model;
    }


    /**
     * Convenient class generating names for EPL variables.
     */
    private static class Alphabet implements Iterator<String> {

        private char current = 'A';

        @Override
        public boolean hasNext() {
            return current < 'Z';
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Alphabet has reach to the end.");
            }
            return String.valueOf(current++);
        }

    }
}
