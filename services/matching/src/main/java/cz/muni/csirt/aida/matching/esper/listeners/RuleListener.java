package cz.muni.csirt.aida.matching.esper.listeners;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.idea.Node;
import cz.muni.csirt.aida.idea.Source;
import cz.muni.csirt.aida.idea.Target;
import cz.muni.csirt.aida.idea.kafka.IdeaSerializer;
import cz.muni.csirt.aida.matching.esper.soda.annotations.StatementType;
import cz.muni.csirt.aida.matching.jmx.Metrics;
import cz.muni.csirt.aida.mining.model.Item;
import cz.muni.csirt.aida.mining.model.Rule;
import cz.muni.csirt.aida.mining.model.Rules;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import static java.util.Collections.singletonList;


public class RuleListener implements UpdateListener {

    private static final Logger logger = LoggerFactory.getLogger(RuleListener.class);
    private static final Metrics metrics = Metrics.getInstance();

    private final Producer<Long, Idea> kafkaProducer;
    private final String outputKafkaTopic;
    private final boolean currentDetectTimeOfPredictions;

    public RuleListener(String kafkaBroker, String kafkaTopic, boolean currentDetectTimeOfPredictions) {
        outputKafkaTopic = kafkaTopic;
        this.currentDetectTimeOfPredictions = currentDetectTimeOfPredictions;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IdeaSerializer.class.getName());
        this.kafkaProducer = new KafkaProducer<>(props);
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime) {
        if (newEvents == null || newEvents.length == 0) {
            logger.warn("Listener called with no new events.");
            return;
        }

        // Obtain Rule object

        cz.muni.csirt.aida.matching.esper.soda.annotations.Rule ruleAnnotation =
                findRuleAnnotation(Arrays.asList(statement.getAnnotations()));
        if (ruleAnnotation == null) {
            throw new RuntimeException("Match on statement without defined @Rule annotation. Is this statement a rule?");
        }
        Rule rule = Rules.fromSpmf(ruleAnnotation.value(), 0, ruleAnnotation.confidence());
        StatementType statementType = ruleAnnotation.type();
        logger.debug("Listener's update is called for {} of rule '{}'", statementType, ruleAnnotation.value());

        // Process new events

        for (EventBean eventBean : newEvents) {

            // Get events on which the predicted event is triggered

            List<Idea> basedOn = Arrays.stream(eventBean.getEventType().getPropertyNames())
                    .sorted()
                    .map(x -> (Idea) eventBean.get(x))
                    .collect(Collectors.toList());

            // Create predicted/observed events and send them to kafka

            switch (statementType) {
                case PREDICTION:
                    for (Item event : rule.getConsequent()) {
                        metrics.getMeasures().computeIfAbsent("predictions", x -> new AtomicInteger()).incrementAndGet();

                        Idea predicted = ideaBase(basedOn);
                        predicted.setDescription(
                                "This event did not happen yet. It is just predicted.");
                        predicted.setNote(ruleAnnotation.value()); // Set string representation of a rule
                        if (rule.getConfidence() != 0) {
                            predicted.setConfidence(rule.getConfidence());
                        }

                        predicted.getCategory().add(event.getCategory());
                        if (event.getPort() != null) {
                            predicted.getTarget().get(0).setPort(Collections.singletonList(event.getPort()));
                        }
                        predicted.setNode(Collections.singletonList(
                                new Node(event.getNodeName(), null, null, null, null)
                        ));

                        if (!currentDetectTimeOfPredictions) {
                            predicted.setDetectTime(getLatestAntecedent(rule, basedOn));
                        }

                        sendToKafka(predicted);
                    }
                    break;

                case OBSERVATION:
                    metrics.getMeasures().computeIfAbsent("observations", x -> new AtomicInteger()).incrementAndGet();

                    Idea observation = ideaBase(basedOn);
                    observation.setDescription(
                            "This event represents a full Rule match, which means that all events in the Rule was " +
                                    "observed and are presented in 'CorrelID'."
                    );
                    observation.setNote(ruleAnnotation.value()); // Set string representation of a rule
                    if (rule.getConfidence() != 0) {
                        observation.setConfidence(rule.getConfidence());
                    }

                    observation.setAdditionalProperty("mitigationTime", getMitigationTime(rule, basedOn));

                    if (!currentDetectTimeOfPredictions) {
                        observation.setDetectTime(getMitigationTime(rule, basedOn));
                    }
                    
                    sendToKafka(observation);
                    break;
            }

        }
    }

    private cz.muni.csirt.aida.matching.esper.soda.annotations.Rule findRuleAnnotation(Iterable<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == cz.muni.csirt.aida.matching.esper.soda.annotations.Rule.class) {
                return (cz.muni.csirt.aida.matching.esper.soda.annotations.Rule) annotation;
            }
        }
        return null;
    }

    /**
     * Create {@link Idea}.
     * @return Idea with filled basic information.
     */
    private static Idea ideaBase(List<Idea> basedOn) {
        Idea idea = new Idea();

        idea.setID(String.valueOf(UUID.randomUUID()));
        idea.setFormat(Idea.Format.IDEA_0);
        idea.setDetectTime(new Date());

        // Set Categories

        idea.setCategory(new ArrayList<>());
        idea.getCategory().add("Test");

        // Set Correlation IDs

        idea.setCorrelID(basedOn.stream().map(Idea::getID).collect(Collectors.toList()));

        // Set Sources

        Source source = new Source();
        source.setIP4(singletonList(basedOn.get(0).getSource().get(0).getIP4().get(0)));
        idea.setSource(singletonList(source));

        // Set Targets

        Target target = new Target();
        try {
            target.setIP4(singletonList(basedOn.get(0).getTarget().get(0).getIP4().get(0)));
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.debug("Missing target IPv4. Predicted/observed event will be without target.");
        }
        idea.setTarget(singletonList(target));

        return idea;
    }

    private void sendToKafka(Idea idea) {
        logger.debug("Idea message '{}' will be send to kafka topic '{}'", idea, outputKafkaTopic);
        ProducerRecord<Long, Idea> record = new ProducerRecord<>(outputKafkaTopic, idea);
        try {
            kafkaProducer.send(record).get(3, TimeUnit.MINUTES);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException(
                    String.format("Unable to send idea message '%s' to kafka topic '%s'.", idea, outputKafkaTopic), e);
        }
    }

    private static long getMitigationTime(Rule rule, List<Idea> basedOn) {
        List<Idea> antecedents = basedOn.subList(0, rule.getAntecedent().size());
        List<Idea> consequences = basedOn.subList(rule.getAntecedent().size(), basedOn.size());

        Date latestAntecedent = antecedents.stream()
                .max(Comparator.comparing(Idea::getDetectTime))
                .get().getDetectTime();

        Date earliestConsequent = consequences.stream()
                .min(Comparator.comparing(Idea::getDetectTime))
                .get().getDetectTime();

        return dateDiff(latestAntecedent, earliestConsequent, TimeUnit.SECONDS);
    }

    private static Date getLatestAntecedent(Rule rule, List<Idea> basedOn) {
        List<Idea> antecedents = basedOn.subList(0, rule.getAntecedent().size());

        return antecedents.stream()
                .max(Comparator.comparing(Idea::getDetectTime))
                .get().getDetectTime();
    }

    private static long dateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

}
