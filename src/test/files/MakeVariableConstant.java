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
public class MakeVariableConstant {
    private static final int constant = 0;  // ok
    private int varDefinedInClassNotReassigned = 0; // Noncompliant {{Make variable constant}}
    private int varDefinedInClassReassigned = 0;    // ok

    void changeVarDefinedInClassReassigned() {
        varDefinedInClassReassigned = 1;
        System.out.println("varDefinedInClassReassigned = " + varDefinedInClassReassigned);
        System.out.println("varDefinedInClassNotReassigned = " + varDefinedInClassNotReassigned);
        System.out.println("constant = " + constant);

    }

    void simpleMethod() {
        String varDefinedInMethodNotReassigned = "hello"; // Noncompliant {{Make variable constant}}
        String varDefinedInMethodReassigned = "hello";  // ok
        varDefinedInMethodReassigned = "bye";

        System.out.println("varDefinedInMethodNotReassigned = " + varDefinedInMethodNotReassigned);
        System.out.println("varDefinedInMethodReassigned = " + varDefinedInMethodReassigned);
    }

    /*
    void methodWithFor() {
        double varDefinedInMethodForNotReassigned = 1.0d; // Noncompliant {{Make variable constant}}
        double varDefinedInMethodForReassigned = 100.0d; // ok

        for (int i = 0; i < 10; i++) {
            varDefinedInMethodForReassigned += i;
        }

        System.out.println("varDefinedInMethodForNotReassigned = " + varDefinedInMethodForNotReassigned);
        System.out.println("varDefinedInMethodForReassigned = " + varDefinedInMethodForReassigned);
    }
     */

}