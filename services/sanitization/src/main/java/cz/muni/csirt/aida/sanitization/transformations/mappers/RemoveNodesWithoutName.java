package cz.muni.csirt.aida.sanitization.transformations.mappers;

import cz.muni.csirt.aida.sanitization.idea.Idea;
import cz.muni.csirt.aida.sanitization.idea.Node;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveNodesWithoutName implements ValueMapper<Idea, Idea> {

    @Override
    public Idea apply(Idea value) {
        if (value.getNode() == null) {
            return value;
        }

        List<Node> withoutNames = value.getNode().stream()
                .filter(node ->
                        node != null &&
                        node.getName() != null &&
                        !node.getName().isEmpty())
                .collect(Collectors.toList());
        value.setNode(withoutNames);

        return value;
    }
}
