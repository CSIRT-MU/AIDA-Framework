package cz.muni.csirt.aida.sanitization.transformations.mappers;

import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.List;
import java.util.stream.Collectors;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.idea.Node;

public class RemoveWardenFilerNodes implements ValueMapper<Idea, Idea> {

    private static final String WARDEN_FILER = "warden_filer";

    @Override
    public Idea apply(Idea value) {
        if (value.getNode() == null || value.getNode().size() <= 1) {
            return value;
        }

        List<Node> withoutWardenFiler = value.getNode().stream()
                .filter(node ->
                        node == null ||
                        node.getName() == null ||
                        !node.getName().contains(WARDEN_FILER))
                .collect(Collectors.toList());
        value.setNode(withoutWardenFiler);

        return value;
    }
}
