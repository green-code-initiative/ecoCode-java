package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class MakeNonReassignedVariablesConstantsTest {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/MakeNonReassignedVariablesConstantsCheck.java")
                .withCheck(new MakeNonReassignedVariablesConstants())
                .verifyIssues();
    }

}