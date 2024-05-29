package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class AvoidEnergyConsumingMethodsTest {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidEnergyConsumingMethodsCheck.java")
                .withCheck(new AvoidEnergyConsumingMethods())
                .verifyIssues();
    }

}