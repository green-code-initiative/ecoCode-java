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

import javax.annotation.Nullable;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.Symbol.VariableSymbol;
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
        List<String> selectedColumns = getSelectedColumns(methodInvocationTree);
        if(selectedColumns.isEmpty()) {
            return;
        }
        List<String> usedColumns = getUsedColumns(methodInvocationTree, selectedColumns);
        if(usedColumns == null) {
            return;
        }
        List<String> differences = selectedColumns.stream()
                    .filter(element -> !usedColumns.contains(element))
                    .collect(Collectors.toList());
        if(!differences.isEmpty()) {
            reportIssue(methodInvocationTree, MESSAGERULE);
        }
    }

    private static List<String> getSelectedColumns(MethodInvocationTree methodInvocationTree) {
        Arguments arguments = methodInvocationTree.arguments();
        if(arguments.isEmpty()) {
            return new ArrayList<>();
        }
        ExpressionTree argument = arguments.get(0);
        LiteralTree litteral = extractLiteralFromVariable(argument);
        if(litteral == null) {
            return new ArrayList<>();
        }
        String query = litteral.value();
        List<String> selectedColumns = extractSelectedSQLColumns(query);
        return selectedColumns;
    }

    @Nullable
    private static List<String> getUsedColumns(MethodInvocationTree methodInvocationTree, List<String> selectedColumns){
        Symbol resultSet = getResultSetNode(methodInvocationTree);

        if(resultSet == null || isResultSetInvalid(resultSet)) {
            return null;
        }

        List<String> usedColumns = new ArrayList<>();
        List<IdentifierTree> resultSetUsages = resultSet.usages();
        for(IdentifierTree usage : resultSetUsages) {
            if(usage.parent().is(Tree.Kind.ASSIGNMENT)){
                break;
            }
            MethodInvocationTree methodInvocation = getMethodInvocationFromTree(usage);
            if(methodInvocation == null ){
                continue;
            }
            if(methodInvocation.arguments().isEmpty()){
                continue;
            }
            ExpressionTree parameter = methodInvocation.arguments().get(0);
            LiteralTree columnGot = extractLiteralFromVariable(parameter);
            if(columnGot == null){
                continue;
            }
            String column;
            String value = columnGot.value();
            if(SQL_RESULTSET_GET_COLNAME.matches(methodInvocation) && columnGot.is(Tree.Kind.STRING_LITERAL)) {
                column = value.toUpperCase();
                column = column.replaceAll("^['\"]", "");
                column = column.replaceAll("['\"]$", "");
            } else if(SQL_RESULTSET_GET_COLID.matches(methodInvocation) && columnGot.is(Tree.Kind.INT_LITERAL)) {
                int columnId = Integer.parseInt(value);
                if(columnId > selectedColumns.size()) {
                    break;
                }
                column = selectedColumns.get(columnId - 1);
            } else {
                continue;
            }
            usedColumns.add(column);
        }
        return usedColumns;
    }

    @Nullable
    private static Symbol getResultSetNode(MethodInvocationTree methodInvocationTree) {
        ExpressionTree et = methodInvocationTree.methodSelect();
        if(!et.is(Tree.Kind.MEMBER_SELECT)) {
            return null;
        }
        MemberSelectExpressionTree mset = (MemberSelectExpressionTree) et;
        ExpressionTree expression = mset.expression();
        if(!expression.is(Tree.Kind.IDENTIFIER)) {
            return null;
        }
        IdentifierTree id = (IdentifierTree) expression;
        Symbol statement = id.symbol();
        if(statement == null) {
            return null;
        }
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
        return resultSet;
    }
    @Nullable
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

    @Nullable
    private static LiteralTree extractLiteralFromVariable(ExpressionTree tree){ 
        if(tree == null || tree instanceof LiteralTree){
            return (LiteralTree) tree;
        }
        if (!tree.is(Tree.Kind.IDENTIFIER)) {
            return null;
        }
        IdentifierTree identifierTree = (IdentifierTree) tree;
        Symbol symbol = identifierTree.symbol();
        if(symbol == null) {
            return null;
        }
        if(!symbol.isFinal()){
            return null;
        }
        if(symbol.isVariableSymbol()) {
            VariableSymbol variableSymbol = (VariableSymbol) symbol;
            Tree assignement = variableSymbol.declaration();
            if(assignement.is(Tree.Kind.VARIABLE)){
                VariableTree variableTree = (VariableTree) assignement;
                ExpressionTree initializer = variableTree.initializer();
                return extractLiteralFromVariable(initializer);
            }
        }
        return null;
    }

    private static boolean isResultSetInvalid(Symbol resultSet) {
        return  isResultSetUsedInMethod(resultSet)
             || isResultSetReassigned(resultSet);
    }

    private static boolean isResultSetUsedInMethod(Symbol resultSet) {
        List<IdentifierTree> resultSetUsages = resultSet.usages();
        for(IdentifierTree usage : resultSetUsages) {
            Tree parent = usage.parent();
            if(parent.is(Tree.Kind.ARGUMENTS)){
                return true;
            }
        }
        return false;
    }

    private static boolean isResultSetReassigned(Symbol resultSet) {
        List<IdentifierTree> resultSetUsages = resultSet.usages();
        for(IdentifierTree usage : resultSetUsages) {
            Tree parent = usage.parent();
            if(parent.is(Tree.Kind.ASSIGNMENT)){
                AssignmentExpressionTree assignment = (AssignmentExpressionTree) parent;
                ExpressionTree expressionTree = assignment.variable();
                if(expressionTree.is(Tree.Kind.IDENTIFIER)){
                    if(resultSet.equals(((IdentifierTree) expressionTree).symbol())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static List<String> extractSelectedSQLColumns(String query){
        if(query == null){
            return new ArrayList<>();
        }
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
