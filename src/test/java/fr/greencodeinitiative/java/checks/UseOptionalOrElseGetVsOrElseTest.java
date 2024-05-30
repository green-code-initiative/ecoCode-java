package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class UseOptionalOrElseGetVsOrElseTest {
    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseOptionalOrElseGetVsOrElse.java")
                .withCheck(new UseOptionalOrElseGetVsOrElse())
                .verifyIssues();
    }
}
