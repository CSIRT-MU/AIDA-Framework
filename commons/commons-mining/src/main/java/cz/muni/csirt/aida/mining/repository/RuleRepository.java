package cz.muni.csirt.aida.mining.repository;

import java.util.Collection;
import java.util.List;

import cz.muni.csirt.aida.mining.model.KeyType;
import cz.muni.csirt.aida.mining.model.Rule;

public interface RuleRepository {

    List<Rule> getActiveRules();

    void saveRules(Collection<Rule> rules, KeyType keyType, int dbSize, String algorithm);
}
