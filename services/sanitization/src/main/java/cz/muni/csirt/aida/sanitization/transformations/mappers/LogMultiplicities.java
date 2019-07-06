package cz.muni.csirt.aida.sanitization.transformations.mappers;

import cz.muni.csirt.aida.sanitization.idea.Idea;
import cz.muni.csirt.aida.sanitization.idea.Source;
import cz.muni.csirt.aida.sanitization.idea.Target;

import org.apache.kafka.streams.kstream.ValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMultiplicities implements ValueMapper<Idea, Idea> {

    private static final Logger logger = LoggerFactory.getLogger(LogMultiplicities.class);

    @Override
    public Idea apply(Idea value) {
        logMultipleNodes(value);

        logMultipleSources(value);
        logMultipleSourceIps(value);

        logMultipleTargets(value);
        logMultipleTargetPorts(value);

        return value;
    }

    private static void logMultipleNodes(Idea idea) {
        if (idea.getNode() != null && idea.getNode().size() > 1) {
            logger.info("Multiple Nodes: {}, Idea ID: {}", idea.getNode().size(), idea.getID());
        }
    }

    private static void logMultipleSources(Idea idea) {
        if (idea.getSource() != null && idea.getSource().size() > 1) {
            logger.info("Multiple Sources: {}, Idea ID: {}", idea.getSource().size(), idea.getID());
        }
    }

    private static void logMultipleSourceIps(Idea idea) {
        if (idea.getSource() != null) {
            for (Source source : idea.getSource()) {
                if (source.getIP4() != null && source.getIP4().size() > 1) {
                    logger.info("Multiple Source Ips: {}, Idea ID: {}", source.getIP4().size(), idea.getID());
                }
            }
        }
    }

    private static void logMultipleTargets(Idea idea) {
        if (idea.getTarget() != null && idea.getTarget().size() > 1) {
            logger.info("Multiple Targets: {}, Idea ID: {}", idea.getTarget().size(), idea.getID());
        }
    }

    private static void logMultipleTargetPorts(Idea idea) {
        if (idea.getTarget() != null) {
            for (Target target : idea.getTarget()) {
                if (target.getPort() != null && target.getPort().size() > 1) {
                    logger.info("Multiple Target Ports: {}, Idea ID: {}", target.getPort().size(), idea.getID());
                }
            }
        }
    }
}
