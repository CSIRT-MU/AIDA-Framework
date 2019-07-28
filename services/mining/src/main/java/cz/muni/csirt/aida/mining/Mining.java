package cz.muni.csirt.aida.mining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTopSeqRules;
import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import cz.muni.csirt.aida.mining.model.KeyType;
import cz.muni.csirt.aida.mining.model.Rule;
import cz.muni.csirt.aida.mining.repository.RuleRepository;
import cz.muni.csirt.aida.mining.repository.SqliteRuleRepository;
import cz.muni.csirt.aida.mining.spmf.IdeaSequenceDatabase;
import cz.muni.csirt.aida.mining.spmf.SequenceDatabases;

public class Mining {

	@Parameter(names = {"--kafka-brokers"}, description = "List of Kafka bootstrap servers separated by comma")
	private String kafkaBrokers = "127.0.0.1:9092";

	@Parameter(names = {"--kafka-consumer-group"}, description = "Kafka consumer group ID")
	private String kafkaConsumerGroupId = "mining";

	@Parameter(names = {"--kafka-input-topic"},
			description = "Kafka topic name for incoming IDEA events",
			required = true)
	private String kafkaInputTopic;

	@Parameter(names = {"--sqlite-url"},
			description = "SQLite URL from which will be rules fetched (example 'jdbc:sqlite:/var/aida/rules/rule.db')",
			required = true)
	private String sqliteUrl;

	@Parameter(names = {"-h", "--help"}, help = true)
	private boolean help;

	public static void main(String[] args) {
		Mining mining = new Mining();

		JCommander jcommander = JCommander.newBuilder()
				.addObject(mining)
				.programName("Mining")
				.build();
		jcommander.parse(args);

		if (mining.help) {
			jcommander.usage();
			return;
		}

		mining.run();
	}

	public void run() {
		// TODO: add info logs

		// Create sequential database

		IdeaSequenceDatabase sequenceDb = SequenceDatabases.fromKafka(kafkaInputTopic, kafkaBrokers,
				kafkaConsumerGroupId, KeyType.SRC_TAR_IPV4);

		// Run algorithm

		AlgoTopSeqRules algo = new AlgoTopSeqRules();
		RedBlackTree<ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule> spmfRules =
				algo.runAlgorithm(10, sequenceDb.getDatabase(), 0.5);

		// Save results into db

		if (spmfRules.isEmpty()) {
			// Have to exit execution because when the RedBlackTree is empty the iterator returns null
			return;
		}

		Collection<Rule> rules = new ArrayList<>();

		for (ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule spmfRule : spmfRules) {
			Rule rule = new Rule(
					Arrays.stream(spmfRule.getItemset1()).mapToObj(sequenceDb.getItemMapping()::get).collect(Collectors.toSet()),
					Arrays.stream(spmfRule.getItemset2()).mapToObj(sequenceDb.getItemMapping()::get).collect(Collectors.toSet()),
					spmfRule.getAbsoluteSupport(),
					spmfRule.getConfidence()
			);
			rules.add(rule);
		}

		RuleRepository ruleRepository = new SqliteRuleRepository(sqliteUrl);
		ruleRepository.saveRules(rules, sequenceDb.getKeyType(), sequenceDb.getDatabase().size(), "TopSeqRules");
	}
}
