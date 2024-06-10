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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.Symbol.VariableSymbol;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

@Rule(key = "1044")
public class UseEveryColumnQueried extends IssuableSubscriptionVisitor {

    protected static final String MESSAGERULE = "Avoid querying SQL columns that are not used";
    private static final String JAVA_SQL_STATEMENT = "java.sql.Statement";
    private static final String JAVA_SQL_RESULTSET = "java.sql.ResultSet";
    private static final MethodMatchers SQL_STATEMENT_DECLARE_SQL = MethodMatchers
        .create()
        .ofSubTypes(JAVA_SQL_STATEMENT)
        //TODO : also take into account addBatch and executeBatch
        .names("executeQuery", "execute", "executeUpdate", "executeLargeUpdate")
        .addParametersMatcher("java.lang.String")
        .build();
    private static MethodMatchers SQL_STATEMENT_RETRIEVE_RESULTSET = MethodMatchers
        .create()
        .ofSubTypes(JAVA_SQL_STATEMENT)
        .names("executeQuery", "getResultSet")
        .withAnyParameters()
        .build();
    private static final MethodMatchers SQL_RESULTSET_GET_COLNAME = MethodMatchers
        .create()
        .ofSubTypes(JAVA_SQL_RESULTSET)
        .names("getInt", "getString", "getLong", "getDouble", "getFloat", "getBoolean", "getByte", "getShort", "getBigDecimal", 
        "getTimestamp", "getDate", "getTime", "getObject", "getArray", "getBlob", "getClob", "getRef", "getRowId", 
        "getNClob", "getSQLXML", "getURL", "getNString", "getNCharacterStream", "getCharacterStream", "getAsciiStream", "getBinaryStream")
        .addParametersMatcher("java.lang.String")
        .build();
    private static final MethodMatchers SQL_RESULTSET_GET_COLID = MethodMatchers
        .create()
        .ofSubTypes(JAVA_SQL_RESULTSET)
        .names("getInt", "getString", "getLong", "getDouble", "getFloat", "getBoolean", "getByte", "getShort", "getBigDecimal", 
        "getTimestamp", "getDate", "getTime", "getObject", "getArray", "getBlob", "getClob", "getRef", "getRowId", 
        "getNClob", "getSQLXML", "getURL", "getNString", "getNCharacterStream", "getCharacterStream", "getAsciiStream", "getBinaryStream")
        .addParametersMatcher("int")
        .build();
    private static final Pattern SELECTED_COLUMNS_PATTERN = Pattern.compile("SELECT\\s+(.*)\\s+FROM\\s+.*");

    @Override
    public List<Kind> nodesToVisit() {
        return Arrays.asList(Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {

        MethodInvocationTree methodInvocationTree = (MethodInvocationTree) tree;
        if(!SQL_STATEMENT_DECLARE_SQL.matches(methodInvocationTree)) {
            return;
        }
        ExpressionTree et = methodInvocationTree.methodSelect();
        if(!et.is(Tree.Kind.MEMBER_SELECT)) {
            return;
        }
        MemberSelectExpressionTree mset = (MemberSelectExpressionTree) et;
        ExpressionTree expression = mset.expression();
        if(!expression.is(Tree.Kind.IDENTIFIER)) {
            return;
        }
        IdentifierTree id = (IdentifierTree) expression;
        Symbol statement = id.symbol();
        if(statement == null) {
            return;
        }

        // STEP 1 : retrieve the selected columns in the query

        Arguments arguments = methodInvocationTree.arguments();
        if(arguments.isEmpty()) {
            return;
        }
        ExpressionTree argument = arguments.get(0);
        String query;
        if(argument.is(Tree.Kind.STRING_LITERAL)){
            query = ((LiteralTree) argument).value();
        } else if(argument.is(Tree.Kind.IDENTIFIER)){
            query = extractValueFromVariable((IdentifierTree) argument);
        } else {
            return;
        }
        if(query == null){
            return;
        }
        List<String> selectedColumns = extractSelectedSQLColumns(query);

        // STEP 2 : retrieve the resultSet object

        List<IdentifierTree> usages = statement.usages();
        Symbol resultSet = null;
        for(IdentifierTree usage : usages) {
            MethodInvocationTree methodInvocation = getMethodInvocationFromTree(usage);
            if(methodInvocation == null ){
                continue;
            }
            if(SQL_STATEMENT_RETRIEVE_RESULTSET.matches(methodInvocation)){
                Tree parent = methodInvocation.parent();
                if(parent.is(Tree.Kind.VARIABLE)){
                    resultSet = ((VariableTree) parent).symbol();
                    break;
                }
            }
        }

        // STEP 2.1 check if the resultSet is used as a parameter of a method
        // if it is, this check this check cannot be applied
        if(resultSet == null) {
            return;
        }
        List<IdentifierTree> resultSetUsages = resultSet.usages();
        for(IdentifierTree usage : resultSetUsages) {
            Tree parent = usage.parent();
            if(parent.is(Tree.Kind.ARGUMENTS)){
                return;
            }
        }

        // STEP 2.2 check if the resultSet is reassigned
        for(IdentifierTree usage : resultSetUsages) {
            Tree parent = usage.parent();
            if(parent.is(Tree.Kind.ASSIGNMENT)){
                AssignmentExpressionTree assignment = (AssignmentExpressionTree) parent;
                ExpressionTree expressionTree = assignment.variable();
                if(expressionTree.is(Tree.Kind.IDENTIFIER)){
                    if(resultSet.equals(((IdentifierTree) expressionTree).symbol())) {
                        return;
                    }
                }
            }
        }

        // STEP 3 : retrieve the columns used from the resultSet object

        List<String> usedColumns = new ArrayList<>();
        for(IdentifierTree usage : resultSetUsages) {
            if(usage.parent().is(Tree.Kind.ASSIGNMENT)){
                break;
            }
            MethodInvocationTree methodInvocation = getMethodInvocationFromTree(usage);
            if(methodInvocation == null ){
                continue;
            }
            if(SQL_RESULTSET_GET_COLNAME.matches(methodInvocation)) {
                ExpressionTree columnET = methodInvocation.arguments().get(0);
                if(!columnET.is(Tree.Kind.STRING_LITERAL)) {
                    continue;
                }
                String column = ((LiteralTree) columnET).value();

                column = column.toUpperCase();
                column = column.replaceAll("^['\"]", "");
                column = column.replaceAll("['\"]$", "");
                usedColumns.add(column);
            } else if(SQL_RESULTSET_GET_COLID.matches(methodInvocation)) {
                ExpressionTree columnET = methodInvocation.arguments().get(0);
                if(!columnET.is(Tree.Kind.INT_LITERAL)) {
                    continue;
                }
                int column = Integer.parseInt(((LiteralTree) columnET).value());
                if(column > selectedColumns.size()) {
                    continue;
                }
                usedColumns.add(selectedColumns.get(column - 1));
            }
        }

        // STEP 4 : compare selected and used columns, report issues
        List<String> differences = selectedColumns.stream()
                    .filter(element -> !usedColumns.contains(element))
                    .collect(Collectors.toList());
        if(!differences.isEmpty()) {
            reportIssue(methodInvocationTree, MESSAGERULE);
        }

    }

    private static MethodInvocationTree getMethodInvocationFromTree(IdentifierTree tree) {
        Tree parent = tree;
        while(parent != null && !parent.is(Tree.Kind.METHOD_INVOCATION) ){
            parent = parent.parent();
        }
        if(parent == null){
            return null;
        }
        return (MethodInvocationTree) parent;
    }

    private static String extractValueFromVariable(IdentifierTree tree){
        Symbol symbol = tree.symbol();
        if(symbol == null) {
            return null;
        }
        //accept this value if it's a final variable or it's a variable that is not reassigned
        if(!symbol.isFinal()){
            List<IdentifierTree> usages = symbol.usages();
            int assignementCount = 0;
            for(IdentifierTree usage : usages) {
                Tree parent = usage.parent();
                if(parent.is(Tree.Kind.ASSIGNMENT)){
                    assignementCount++;
                }
            }
            if(assignementCount > 1){
                return null;
            }
        }
        if(symbol.isVariableSymbol()) {
            VariableSymbol variableSymbol = (VariableSymbol) symbol;
            Type type = variableSymbol.type();
            if(type.is("java.lang.String")) {
                Tree assignement = variableSymbol.declaration();
                if(assignement.is(Tree.Kind.VARIABLE)){
                    VariableTree variableTree = (VariableTree) assignement;
                    ExpressionTree initializer = variableTree.initializer();
                    if(initializer.is(Tree.Kind.STRING_LITERAL)){
                        return ((LiteralTree) initializer).value();
                    }
                }
            }
        }
        return null;
    }

    static List<String> extractSelectedSQLColumns(String query){
        query = query.toUpperCase();
        query = query.replaceAll("^['\"]", "");
        query = query.replaceAll("['\"]$", "");
        List<String> columns = new ArrayList<>();
        Matcher matcher = SELECTED_COLUMNS_PATTERN.matcher(query);
        if (matcher.matches()) {
            String columnString = matcher.group(1);
            columns = Arrays.asList(columnString.split(","));
            columns.replaceAll(column -> column.replaceAll("\\s+", " "));
            columns.replaceAll(column -> column.contains(" AS ") ? column.split(" AS ")[1].trim() : column.trim());
        }
        return columns;
    }
    
}
