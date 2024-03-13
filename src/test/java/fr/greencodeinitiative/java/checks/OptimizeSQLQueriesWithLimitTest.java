package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

public class OptimizeSQLQueriesWithLimitTest {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/OptimizeSQLQueriesWithLimit.java")
                .withCheck(new OptimizeSQLQueriesWithLimit())
                .verifyIssues();
    }

}
