package fr.greencodeinitiative.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.InputFileScannerContext;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This rule aims to identify and flag methods in Java code that have high energy consumption
 * due to the use of computationally expensive operations. The goal is to encourage developers
 * to write more energy-efficient code by limiting the use of costly operations within a single method.
 * <br>
 * <br>
 *
 * The rule works by assigning a "cost" to specific expensive operations commonly recognized for their
 * high computational demands. These operations include:
 * <ul>
 *   <li>Reflection operations (e.g., {@code Class.forName()}, {@code Method.invoke()})</li>
 *   <li>Synchronization operations (e.g., {@code synchronized} blocks or methods)</li>
 *   <li>I/O operations (e.g., {@code FileInputStream}, {@code FileOutputStream}, {@code BufferedReader}, etc.)</li>
 *   <li>SQL operations (e.g., {@code Statement.execute()}, {@code ResultSet.next()})</li>
 *   <li>String operations (e.g., {@code String.replaceAll()}, {@code String.split()}, etc.)</li>
 *   <li>...</li>
 * </ul>
 * <br>
 *
 * For each occurrence of these operations within a method, a predefined number of points is assigned.
 * If the total points for a method exceed a certain threshold (e.g., 5 points), the method is considered
 * to be energy-inefficient. A warning is then issued to indicate that the method may have high energy
 * consumption and suggest the developer to refactor or optimize the method.
 * <br>
 * <br>
 *
 * Example:
 * <pre>
 * {@code
 * public class Example {
 *     public void exampleMethod() {
 *         String data = "example";
 *         data = data.replaceAll("e", "a"); // 1 point
 *         FileInputStream fis = new FileInputStream("file.txt"); // 1 point
 *         // Other operations
 *     }
 * }
 * }
 * </pre>
 * <br>
 * In the above example, the `exampleMethod` uses `String.replaceAll()` and `FileInputStream`,
 * each contributing 1 point to the total score. If additional expensive operations are added
 * and the total score exceeds 5 points, the rule will flag this method.
 * <br>
 * <br>
 *
 * This rule helps developers identify potential performance bottlenecks and encourages
 * the adoption of more efficient coding practices to reduce the energy footprint of their applications.
 *
 * @author Massil TAGUEMOUT - CGI FRANCE
 */
@Rule(key = "EC1245")
@DeprecatedRuleKey(repositoryKey = "greencodeinitiative-java", ruleKey = "EC1245")
public class AvoidEnergyConsumingMethods extends IssuableSubscriptionVisitor {

    private static final int ENERGY_THRESHOLD = 5;
    private static final Map<String, Integer> COSTLY_METHODS = new HashMap<>();

    static {
        // Reference costly methods in the map and their points
        // Reflection methods
        COSTLY_METHODS.put("java.lang.Class.forName", 1);
        COSTLY_METHODS.put("java.lang.reflect.Method.invoke", 1);
        COSTLY_METHODS.put("java.lang.reflect.Field.get", 1);
        COSTLY_METHODS.put("java.lang.reflect.Field.set", 1);
        COSTLY_METHODS.put("java.lang.reflect.Constructor.newInstance", 1);
        COSTLY_METHODS.put("java.lang.Class.getMethods", 1);
        COSTLY_METHODS.put("java.lang.Class.getDeclaredMethods", 1);
        COSTLY_METHODS.put("java.lang.Class.getFields", 1);
        COSTLY_METHODS.put("java.lang.Class.getDeclaredFields", 1);
        COSTLY_METHODS.put("java.lang.Class.getConstructors", 1);
        COSTLY_METHODS.put("java.lang.Class.getDeclaredConstructors", 1);

        // String methods
        COSTLY_METHODS.put("java.lang.String.concat", 1);
        COSTLY_METHODS.put("java.lang.String.substring", 1);
        COSTLY_METHODS.put("java.lang.String.replace", 1);
        COSTLY_METHODS.put("java.lang.String.matches", 1);
        COSTLY_METHODS.put("java.lang.String.split", 1);

        // Collections
        COSTLY_METHODS.put("java.util.Vector.add", 1);
        COSTLY_METHODS.put("java.util.Vector.get", 1);
        COSTLY_METHODS.put("java.util.Vector.remove", 1);
        COSTLY_METHODS.put("java.util.Hashtable.put", 1);
        COSTLY_METHODS.put("java.util.Hashtable.get", 1);
        COSTLY_METHODS.put("java.util.Hashtable.remove", 1);
        COSTLY_METHODS.put("java.util.Collections.synchronizedList", 1);
        COSTLY_METHODS.put("java.util.Collections.synchronizedMap", 1);
        COSTLY_METHODS.put("java.util.Collections.synchronizedSet", 1);
        COSTLY_METHODS.put("java.util.Collections.synchronizedSortedMap", 1);
        COSTLY_METHODS.put("java.util.Collections.synchronizedSortedSet", 1);

        // File I/O
        COSTLY_METHODS.put("java.io.FileInputStream.read", 1);
        COSTLY_METHODS.put("java.io.FileOutputStream.write", 1);
        COSTLY_METHODS.put("java.io.FileReader.read", 1);
        COSTLY_METHODS.put("java.io.FileWriter.write", 1);
        COSTLY_METHODS.put("java.io.RandomAccessFile.read", 1);
        COSTLY_METHODS.put("java.io.RandomAccessFile.write", 1);
        COSTLY_METHODS.put("java.nio.file.Files.readAllBytes", 1);
        COSTLY_METHODS.put("java.nio.file.Files.write", 1);

        // Network I/O
        COSTLY_METHODS.put("java.net.Socket.getInputStream", 1);
        COSTLY_METHODS.put("java.net.Socket.getOutputStream", 1);
        COSTLY_METHODS.put("java.net.ServerSocket.accept", 1);
        COSTLY_METHODS.put("java.net.HttpURLConnection.connect", 1);
        COSTLY_METHODS.put("java.net.HttpURLConnection.getInputStream", 1);
        COSTLY_METHODS.put("java.net.HttpURLConnection.getOutputStream", 1);

        // JNI
        COSTLY_METHODS.put("java.lang.System.loadLibrary", 1);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return List.of(Tree.Kind.METHOD);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD)) {
            MethodTree methodTree = (MethodTree) tree;
            int score = calculateMethodEnergyScore(methodTree);

            if (score > ENERGY_THRESHOLD) {
                reportIssue(methodTree.simpleName(),
                        "This method is considered energy-consuming with a score of " + score
                                + "(maximum score of " + ENERGY_THRESHOLD + " recommended)");
            }
        }
    }

    private int calculateMethodEnergyScore(MethodTree methodTree) {
        final AvoidEnergyConsumingMethodsVisitor visitor = new AvoidEnergyConsumingMethodsVisitor();
        methodTree.accept(visitor);
        return visitor.getScore();
    }

    @Override
    public boolean scanWithoutParsing(InputFileScannerContext inputFileScannerContext) {
        return super.scanWithoutParsing(inputFileScannerContext);
    }


    private static class AvoidEnergyConsumingMethodsVisitor extends BaseTreeVisitor {

        private int score = 0;

        @Override
        public void visitMethodInvocation(MethodInvocationTree methodInvocationTree) {
            Symbol symbol = methodInvocationTree.methodSymbol();
            String fullyQualifiedName = symbol.owner().type().fullyQualifiedName() + "." + symbol.name();

            score += COSTLY_METHODS.getOrDefault(fullyQualifiedName, 0);
            super.visitMethodInvocation(methodInvocationTree);
        }

        public int getScore() {
            return score;
        }

    }
}
