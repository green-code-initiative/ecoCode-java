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
class IncrementCheckBinaryExpression {
    IncrementCheckBinaryExpression(IncrementCheckBinaryExpression mc) {
    }
    void unaryExpressionWithinBinaryExpression() {
        var i = 0;
        for (int j=0; j < 10; ++j) {
            System.out.println("test" + i++);
            System.out.println(2 + i++);
            System.out.println(2 - i++);
            System.out.println(2 * i++);
            System.out.println(2 / i++);
        }
    }
}