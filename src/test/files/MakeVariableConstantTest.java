public class MakeVariableConstantTest {

    public void testEc82() {
        String test = "test"; // Noncompliant {{A variable is never reassigned and can be made constant}}
        System.out.println("Hello World");
    }
}
