package cz.muni.csirt.aida.sanitization.transformations.mappers;

import cz.muni.csirt.aida.sanitization.idea.Idea;
import cz.muni.csirt.aida.sanitization.idea.Node;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.List;
import java.util.stream.Collectors;

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
