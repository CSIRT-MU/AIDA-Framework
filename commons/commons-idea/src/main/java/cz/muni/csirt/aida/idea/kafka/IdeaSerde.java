package cz.muni.csirt.aida.idea.kafka;

import org.apache.kafka.common.serialization.Serdes;

import cz.muni.csirt.aida.idea.Idea;

public class IdeaSerde extends Serdes.WrapperSerde<Idea> {

    public IdeaSerde() {
        super(new IdeaSerializer(), new IdeaDeserializer());
    }
}
