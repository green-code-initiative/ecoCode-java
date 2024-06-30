package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

public class MakeVariableConstantTest {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/MakeVariableConstantTest.java")
                .withCheck(new MakeVariableConstant())
                .verifyIssues();
    }
}


