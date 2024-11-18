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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * In this test case, the ResultSet is passed through a method
 * All Fields are accessed, so no issue is raised
 */
public class UseMethodCompliant {

	private static final String DB_URL = "jdbc:mysql://localhost/TEST";
	private static final String USER = "guest";
	private static final String PASS = "guest123";

	public void callJdbc() {

		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT id, first, last, age FROM Registration");) {
			extractGet(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void extractGet(ResultSet rs) throws SQLException {
		while (rs.next()) {
			// Display values
			System.out.print("ID: " + rs.getInt("id"));
			System.out.print(", Age: " + rs.getInt("age"));
			System.out.print(", First: " + rs.getString("first"));
			System.out.println(", Last: " + rs.getString("last"));
		}
	}
}
