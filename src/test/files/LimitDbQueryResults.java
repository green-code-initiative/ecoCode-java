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

class LimitDbQueryResults {
    LimitDbQueryResults(LimitDbQueryResults mc) {
    }

    public void literalSQLrequest() {
        dummyCall("SELECT id, name, email FROM customers LIMIT 10;");
        dummyCall("SELECT TOP 5 * FROM products;");
        dummyCall("SELECT id, name, email FROM customers WHERE id = 1;");
        dummyCall("SELECT * FROM orders FETCH FIRST 20 ROWS ONLY;");
        dummyCall("WITH numbered_customers AS (SELECT *, ROW_NUMBER() OVER (ORDER BY customer_id) AS row_num FROM customers) SELECT * FROM numbered_customers WHERE row_num <= 50;");

        dummyCall("SELECT * FROM bikes;"); // Noncompliant {{Try and limit the number of data returned for a single query (by using the LIMIT keyword for example)}}
        dummyCall("SELECT id FROM customers;"); // Noncompliant {{Try and limit the number of data returned for a single query (by using the LIMIT keyword for example)}}
    }

    private String dummyCall(String request) {
        return request;
    }
   
}