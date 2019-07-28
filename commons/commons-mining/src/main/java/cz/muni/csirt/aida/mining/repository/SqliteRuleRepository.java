package cz.muni.csirt.aida.mining.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.muni.csirt.aida.mining.model.KeyType;
import cz.muni.csirt.aida.mining.model.Rule;
import cz.muni.csirt.aida.mining.model.Rules;

public class SqliteRuleRepository implements RuleRepository {

    private static final int QUERY_TIMEOUT = 20;

    private String sqliteUrl;

    public SqliteRuleRepository(String sqliteUrl) {
        this.sqliteUrl = sqliteUrl;
    }

    @Override
    public List<Rule> getActiveRules() {
        try (Connection connection = DriverManager.getConnection(sqliteUrl);
                Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(QUERY_TIMEOUT);

            try (ResultSet rs = statement.executeQuery("select rule, support, confidence from rule where rule.active == 1")) {
                List<Rule> rules = new ArrayList<>();
                while(rs.next()) {
                    rules.add(Rules.fromSpmf(
                            rs.getString("rule"),
                            rs.getInt("support"),
                            rs.getDouble("confidence")));
                }
                return rules;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void saveRules(Collection<Rule> rules, KeyType keyType, int dbSize, String algorithm) {
        final String sql = "INSERT INTO rule (rule, support, number_of_sequences, confidence, database, algorithm) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(sqliteUrl)) {

            for (Rule rule : rules) {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setQueryTimeout(QUERY_TIMEOUT);
                    statement.setString(1, Rules.toSpmf(rule));
                    statement.setInt(2, rule.getSupport());
                    statement.setInt(3, dbSize);
                    statement.setDouble(4, rule.getConfidence());
                    statement.setString(5, keyType.toString());
                    statement.setString(6, algorithm);
                    statement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
