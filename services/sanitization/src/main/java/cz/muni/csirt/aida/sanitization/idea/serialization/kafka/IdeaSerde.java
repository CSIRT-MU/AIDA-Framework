package cz.muni.csirt.aida.sanitization.idea.serialization.kafka;

import cz.muni.csirt.aida.sanitization.idea.Idea;
import org.apache.kafka.common.serialization.Serdes;

public class IdeaSerde extends Serdes.WrapperSerde<Idea> {

    public IdeaSerde() {
        super(new IdeaSerializer(), new IdeaDeserializer());
    }
}
