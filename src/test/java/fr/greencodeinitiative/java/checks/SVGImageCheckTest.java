package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;


class SVGImageCheckTest {

    @Test
   public void test1() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/CheckSvg.java")
                .withCheck(new SVGImageCheck())
                .verifyIssues();
    }


    @Test
    public void test2() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/CheckSvg2.java")
                .withCheck(new SVGImageCheck())
                .verifyNoIssues();
    }

    @Test
    public void test3() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/CheckSvg3.java")
                .withCheck(new SVGImageCheck())
                .verifyNoIssues();
    }
}