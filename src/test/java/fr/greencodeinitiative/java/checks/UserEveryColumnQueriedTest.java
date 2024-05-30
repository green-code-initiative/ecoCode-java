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

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class UserEveryColumnQueriedTest {

    @Test
    void testHasIssues() {
        CheckVerifier.newVerifier()
                .onFiles("src/test/files/UseEveryColumnQueriedNonCompliant1.java",
                "src/test/files/UseEveryColumnQueriedNonCompliant2.java",
                "src/test/files/UseEveryColumnQueriedNonCompliant3.java",
                "src/test/files/UseEveryColumnQueriedNonCompliant4.java",
                "src/test/files/UseEveryColumnQueriedNonCompliant5.java",
                "src/test/files/UseEveryColumnQueriedNonCompliant6.java")
                .withCheck(new UseEveryColumnQueried())
                .verifyIssues();
    }

    @Test
    void testHasNoIssues() {
        CheckVerifier.newVerifier()
                .onFiles(
                        "src/test/files/UseEveryColumnQueriedCompliant1.java",
                        "src/test/files/UseEveryColumnQueriedCompliant2.java",
                        "src/test/files/UseEveryColumnQueriedCompliant3.java",
                        "src/test/files/UseEveryColumnQueriedCompliant4.java",
                        "src/test/files/UseEveryColumnQueriedCompliant5.java",
                        "src/test/files/UseEveryColumnQueriedCompliant6.java"
                )
                .withCheck(new UseEveryColumnQueried())
                .verifyNoIssues();
    }
}
