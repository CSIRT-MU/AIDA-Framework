package cz.muni.csirt.aida.matching.esper.soda;


import com.espertech.esper.common.client.soda.AnnotationPart;
import com.espertech.esper.common.client.soda.CreateWindowClause;
import com.espertech.esper.common.client.soda.DotExpression;
import com.espertech.esper.common.client.soda.EPStatementObjectModel;
import com.espertech.esper.common.client.soda.Expression;
import com.espertech.esper.common.client.soda.Expressions;
import com.espertech.esper.common.client.soda.FilterStream;
import com.espertech.esper.common.client.soda.FromClause;
import com.espertech.esper.common.client.soda.InsertIntoClause;
import com.espertech.esper.common.client.soda.SelectClause;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cz.muni.csirt.aida.mining.model.Item;

public class IdeaWindows {

    private static final String NAME = "IdeaWindow";

    private IdeaWindows() {
    }

    /**
     * Get from clause for IdeaWindow
     *
     * @return FromClause
     */
    public static FromClause fromIdeaWindow() {
        return new FromClause(FilterStream.create(NAME));
    }

    public static FromClause fromIdeaWindowFiltered(Collection<Item> events) {
        if (events.isEmpty()) {
            return fromIdeaWindow();
        }

        Expression filter = Expressions.or();
        for (Item event : events) {
            filter.getChildren().add(IdeaExpressions.eventConditions(event, null));
        }

        // Disjunction cannot be compiled with just one expression
        if (filter.getChildren().size() == 1) {
            filter = filter.getChildren().get(0);
        }

        return new FromClause(FilterStream.create(NAME, filter));
    }

    /**
     * Creates statement creating Idea window
     *      create window IdeaWindow.ext:time_order(detectTime.getTime(),1 hours) as select * from Idea
     * @return statement
     */
    public static EPStatementObjectModel createIdeaWindow() {

        // Create window clause

        CreateWindowClause window = CreateWindowClause.create("IdeaWindow");
        window.setAsEventTypeName("Idea");

        // Add views into window clause

        DotExpression detectTime = new DotExpression(Expressions.property("detectTime"));
        detectTime.add("getTime", Collections.emptyList(), false);

        List<Expression> viewParams = Arrays.asList(
//                Expressions.property("detectTime.getTime()"),
                detectTime,
                Expressions.timePeriod(null, 1, null, null, null)
        );
        window.addView("ext", "time_order", viewParams);

        // Create model

        EPStatementObjectModel model = new EPStatementObjectModel();
        model.setCreateWindow(window);
        model.setSelectClause(SelectClause.createWildcard());
        model.fromClause(FromClause.create(FilterStream.create("Idea")));

        // Make statement public

        model.setAnnotations(Collections.singletonList(new AnnotationPart("public")));

        return model;
    }

    /**
     * Creates statement which inserts filtered Idea events into IdeaWindow.
     * Events with missing source IPv4 or target IPv4 are filtered.
     * Events marked as 'Duplicate' or 'Continuing' are also filtered.
     *
     *      insert into IdeaWindow select * from Idea where
     *          (source is not null and source.where(s => s.IP4 is not null).anyOf(s => s.IP4.countOf() > 0) ) and
     *          (target is not null and target.where(s => s.IP4 is not null).anyOf(s => s.IP4.countOf() > 0) ) and
     *          (
     *              additionalProperties is null or
     *              additionalProperties.get('_aida') is null or
     *              (
     *                  cast(additionalProperties.get('_aida'), java.util.Map).get('Duplicate') is null and
     *                  cast(additionalProperties.get('_aida'), java.util.Map).get('Continuing') is null
     *              )
     *          )
     * @return statement
     */
    public static EPStatementObjectModel insertIntoIdeaWindow() {

        EPStatementObjectModel model = new EPStatementObjectModel();
        model.insertInto(new InsertIntoClause(NAME));
        model.selectClause(SelectClause.createWildcard());
        model.fromClause(new FromClause(FilterStream.create("Idea")));

        model.whereClause(
                Expressions.and(
                        IdeaExpressions.hasSourceIPv4(),
                        IdeaExpressions.hasTargetIPv4(),
                        IdeaExpressions.hasNotAidaProperties(Arrays.asList("Duplicate", "Continuing"))
                )
        );

        return model;
    }

}
