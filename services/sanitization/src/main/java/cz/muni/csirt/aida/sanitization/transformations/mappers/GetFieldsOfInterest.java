package cz.muni.csirt.aida.sanitization.transformations.mappers;

import org.apache.kafka.streams.kstream.ValueMapper;

import cz.muni.csirt.aida.idea.Idea;

public class GetFieldsOfInterest implements ValueMapper<Idea, Idea> {

    @Override
    public Idea apply(Idea value) {
        Idea newIdea = new Idea();
        newIdea.setFormat(value.getFormat());
        newIdea.setID(value.getID());
        newIdea.setDetectTime(value.getDetectTime());
        newIdea.setCategory(value.getCategory());
        newIdea.setSource(value.getSource());
        newIdea.setTarget(value.getTarget());
        newIdea.setNode(value.getNode());
        return newIdea;
    }
}
