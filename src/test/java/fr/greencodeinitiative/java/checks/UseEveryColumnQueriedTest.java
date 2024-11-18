/*
 * ecoCode - Java language - Provides rules to reduce the environmental footprint of your Java programs
 * Copyright © 2023 Green Code Initiative (https://www.ecocode.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.greencodeinitiative.java.checks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class UseEveryColumnQueriedTest {

    @Test
    void testExtractSelectedSQLColumns(){
        String query = "\"SELECT id AS registration_id,\tfirst, last as Final, AGE FROM Registration\"";
        List<String> columns = UseEveryColumnQueried.extractSelectedSQLColumns(query);
        assertEquals(4, columns.size());
        assertEquals("REGISTRATION_ID", columns.get(0));
        assertEquals("FIRST", columns.get(1));
        assertEquals("FINAL", columns.get(2));
        assertEquals("AGE", columns.get(3));
    }

    @Test
    void testHasIssues1() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/AttributeQueryNonCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyIssues();
    }

    @Test
    void testHasIssues2() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/LitteralQueryNonCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyIssues();
    }

    @Test
    void testHasIssues3() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/UseColumnIdsAndNameAttributesNonCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyIssues();
    }

    @Test
    @Disabled // case not handled (multiple queries with the same ResultSet object)
    void testHasIssues4() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/MultipleQueriesNonCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyIssues();
    }

    @Test
    @Disabled // case not handled (usage of a method)
    void testHasIssues5() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/UseMethodNonCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyIssues();
    }


    @Test
    void testHasNoIssues1() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/AttributeQueryCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyNoIssues();
    }

    @Test
    void testHasNoIssues2() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/LitteralQueryCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyNoIssues();
    }

    @Test
    void testHasNoIssues3() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/UseColumnIdsAndNameAttributesCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyNoIssues();
    }

    @Test
    void testHasNoIssues4() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/MultipleQueriesCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyNoIssues();
    }

    @Test
    void testHasNoIssues5() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/UseMethodCompliant.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyNoIssues();
    }

    @Test
    void testHasNoIssues6() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/UseEveryColumnQueried/SelectStar.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyNoIssues();
    }
}
