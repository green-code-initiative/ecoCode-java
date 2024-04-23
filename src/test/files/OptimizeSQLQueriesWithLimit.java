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
class OptimizeSQLQueriesWithLimit {

    public void literalSQLrequest() {
        dummyCall("SELECT user FROM myTable"); // Noncompliant {{Optimize Database SQL Queries (Clause LIMIT / WHERE)}}
        dummyCall("SELECT user FROM myTable LIMIT 50"); // Compliant
        dummyCall("SELECT user FROM myTable WHERE user.name = 'titi'"); // Compliant
    }

    @Query("select t from Todo t") // Noncompliant {{Optimize Database SQL Queries (Clause LIMIT / WHERE)}}
    @Query("select t from Todo t where t.status != 'COMPLETED'") // Compliant
    @Query("select t from Todo t where t.status != 'COMPLETED' LIMIT 25") // Compliant

    private void callQuery() {
        String sql1 = "SELECT user FROM myTable"; // Noncompliant {{Optimize Database SQL Queries (Clause LIMIT / WHERE)}}
        String sql2 = "SELECT user FROM myTable LIMIT 50"; // Compliant
        String sql3 = "SELECT user FROM myTable WHERE user.name = 'titi'"; // Compliant
    }

    private void dummyCall(String request) {
    }
}