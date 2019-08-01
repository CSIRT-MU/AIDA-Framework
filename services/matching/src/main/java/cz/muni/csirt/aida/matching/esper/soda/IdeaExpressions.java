package cz.muni.csirt.aida.matching.esper.soda;

import com.espertech.esper.common.client.soda.Conjunction;
import com.espertech.esper.common.client.soda.DotExpression;
import com.espertech.esper.common.client.soda.Expression;
import com.espertech.esper.common.client.soda.Expressions;
import com.espertech.esper.common.client.soda.LambdaExpression;
import com.espertech.esper.common.client.soda.PropertyValueExpression;

import java.util.Collections;
import java.util.List;

import cz.muni.csirt.aida.mining.model.Item;

public class IdeaExpressions {

    private IdeaExpressions() {
    }

    /**
     * Express event as conjunction of conditions describing the event.
     * @param event event
     * @param ideaVar name of the variable representing IDEA event on which the conditions should apply
     * @return conditions describing the event
     */
    static Expression eventConditions(Item event, String ideaVar) {
        return Expressions.and(
                containsCategory(event.getCategory(), ideaVar),
                containsNode(event.getNodeName(), ideaVar),
                containsPort(event.getPort(), ideaVar)
        );
    }

    private static PropertyValueExpression ideaProperty(String propertyName, String ideaVar) {
        if (ideaVar == null || ideaVar.isEmpty()) {
            return Expressions.property(propertyName);
        }
        return Expressions.property(ideaVar + "." + propertyName);
    }

    /**
     * Returns following expression:
     *      {@code ideaVar}.category.contains({@code category})
     * @param category Category
     * @param ideaVar EPL variable representing IDEA event.
     * @return expression defined above
     */
    public static Expression containsCategory(String category, String ideaVar) {
        DotExpression dotExpression = new DotExpression(ideaProperty("category", ideaVar));
        dotExpression.add("contains", Collections.singletonList(Expressions.constant(category, String.class)));

        return dotExpression;
    }

    /**
     * Returns following expression:
     *      {@code ideaVar}.node.anyOf(x => x.name = {@code name})
     * @param name Node name
     * @param ideaVar EPL variable representing IDEA event.
     * @return expression defined above
     */
    public static Expression containsNode(String name, String ideaVar) {
        LambdaExpression anyOfLambda = new LambdaExpression(Collections.singletonList("x"));
        anyOfLambda.addChild(Expressions.eq("x.name", name));

        DotExpression expression = new DotExpression(ideaProperty("node", ideaVar));
        expression.add("anyOf", Collections.singletonList(anyOfLambda));

        return expression;
    }

    /**
     * Returns following expression if {@code port} is not null:
     *      {@code ideaVar}.target.anyOf(x => x.port.contains({@code port}))
     * or following expression if {@code port} is null:
     *      {@code ideaVar}.target is null or
     *      {@code ideaVar}.target.where(x => x.port is not null).countOf(x => x.port.countOf() > 0) = 0
     * @param port wanted port or {@code null} if no port should be present
     * @param ideaVar EPL variable representing IDEA event.
     * @return expression defined above
     */
    public static Expression containsPort(Integer port, String ideaVar) {
        PropertyValueExpression target = ideaProperty("target", ideaVar);

        if (port != null) {
            LambdaExpression anyOfLambda = new LambdaExpression(Collections.singletonList("x"));
            DotExpression dotExpression = new DotExpression(Expressions.property("x.port"));
            dotExpression.add("contains", Collections.singletonList(Expressions.constant(port)));
            anyOfLambda.addChild(dotExpression);

            DotExpression expression = new DotExpression(target);
            expression.add("anyOf", Collections.singletonList(anyOfLambda));

            return expression;
        }

        DotExpression expression = new DotExpression(target);

        // where lambda: (x => x.port is not null)
        LambdaExpression whereLambda = new LambdaExpression(Collections.singletonList("x"));
        whereLambda.addChild(Expressions.isNotNull("x.port"));
        expression.add("where", Collections.singletonList(whereLambda));

        // dotExpr:  x.port.countOf()
        DotExpression portCountOf = new DotExpression(Expressions.property("x.port"));
        portCountOf.add("countOf", Collections.emptyList());

        // countOf lambda: (x => x.port.countOf() > 0)
        LambdaExpression countOfLambda = new LambdaExpression(Collections.singletonList("x"));
        countOfLambda.addChild(Expressions.gt(portCountOf, Expressions.constant(0, int.class)));
        expression.add("countOf", Collections.singletonList(countOfLambda));

        return Expressions.or(Expressions.isNull(target), Expressions.eq(expression, Expressions.constant(0)));
    }

    /**
     * source[0].IP4[0]
     * @return expression
     */
    public static PropertyValueExpression sourceFirstIPv4() {
        return firstIPv4("source");
    }

    /**
     * target[0].IP4[0]
     * @return expression
     */
    public static PropertyValueExpression targetFirstIPv4() {
        return firstIPv4("target");
    }

    /**
     * {@code property}[0].IP4[0]
     * @param property source or target
     * @return expression
     */
    private static PropertyValueExpression firstIPv4(String property) {
        return Expressions.property(property + "[0].IP4[0]");
    }


    /**
     * The expression checks that there is at last one IPv4 inside array of sources.
     * @return Expression
     */
    public static Expression hasSourceIPv4() {
        return hasIPv4("source");
    }

    /**
     * The expression checks that there is at last one IPv4 inside array of targets.
     * @return
     */
    public static Expression hasTargetIPv4() {
        return hasIPv4("target");
    }

    /**
     * Creates following expression. The expression checks that there is at last one IPv4 inside specified array of
     * sources or targets.
     *      {@code property} is not null and
     *      {@code property}.where(x => x.IP4 is not null).anyOf(x => x.IP4.countOf() > 0)
     * @param property Property holding array of 'source' or 'target' IDEA objects
     * @return Expression
     */
    private static Expression hasIPv4(String property) {
        DotExpression expression = new DotExpression(Expressions.property(property));

        // where lambda: (s => s.IP4 is not null)
        LambdaExpression whereLambda = new LambdaExpression(Collections.singletonList("x"));
        whereLambda.addChild(Expressions.isNotNull("x.IP4"));
        expression.add("where", Collections.singletonList(whereLambda));

        // dotExpr:  s.IP4.countOf()
        DotExpression ip4countOf = new DotExpression(Expressions.property("x.IP4"));
        ip4countOf.add("countOf", Collections.emptyList());

        // onyOf lambda: (s => s.IP4.countOf() > 0)
        LambdaExpression anyOfLambda = new LambdaExpression(Collections.singletonList("x"));
        anyOfLambda.addChild(Expressions.gt(ip4countOf, Expressions.constant(0, int.class)));
        expression.add("anyOf", Collections.singletonList(anyOfLambda));

        return Expressions.and(Expressions.isNotNull(property), expression);
    }

    /**
     * Get '_aida' property.
     *      additionalProperties.get('_aida')
     * @return expression
     */
    public static Expression aida() {
        DotExpression dotExpression = new DotExpression(Expressions.property("additionalProperties"));
        dotExpression.add("get",
                Collections.singletonList(Expressions.constant("_aida", String.class)));
        return dotExpression;
    }

    /**
     * Get property nested inside '_aida'.
     *      cast(additionalProperties.get('_aida'), java.util.Map).get({@code property})
     * @param property property name
     * @return expression
     */
    public static Expression aida(String property) {
        DotExpression dotExpression = new DotExpression(Expressions.cast(aida(), "java.util.Map"));
        dotExpression.add("get", Collections.singletonList(Expressions.constant(property, String.class)));
        return dotExpression;
    }

    /**
     * Construct expression which is true when given aida properties are missing.
     *
     * Example for list of properties ["Duplicate", "Continuing"]:
     *
     * ( additionalProperties is null
     *  or additionalProperties.get('_aida') is null
     *  or (
     *          cast(additionalProperties.get('_aida'), java.util.Map).get('Duplicate') is null and
     *          cast(additionalProperties.get('_aida'), java.util.Map).get('Continuing') is null
     *      )
     * )
     *
     * @param properties properties that should be missing
     * @return expression
     */
    public static Expression hasNotAidaProperties(List<String> properties) {
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("Properties cannot be empty.");
        }

        Conjunction propertiesAreNull = Expressions.and();
        for (String property : properties) {
            propertiesAreNull.addChild(Expressions.isNull(aida(property)));
        }

        return Expressions.or(
                Expressions.isNull("additionalProperties"),
                Expressions.isNull(aida()),
                propertiesAreNull
        );
    }

}
