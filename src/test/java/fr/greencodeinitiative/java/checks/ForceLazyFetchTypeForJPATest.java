package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class ForceLazyFetchTypeForJPATest {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/ForceLazyFetchTypeForJPACheck.java")
                .withCheck(new ForceLazyFetchTypeForJPA())
                .verifyIssues();
    }

}