package fr.greencodeinitiative.java.checks;

public class AvoidUsingGlobalVariablesCheck {
    public static double price = 15.24; // Noncompliant {{Avoid using global variables}}
    public static long pages = 1053; // Noncompliant {{Avoid using global variables}}

    public static void main(String[] args) {
        double newPrice = AvoidUsingGlobalVariablesCheck.price;
        long newPages = AvoidUsingGlobalVariablesCheck.pages;
        System.out.println(newPrice);
        System.out.println(newPages);
    }
    static{ // Noncompliant {{Avoid using global variables}}
        int a = 4;
    }

    public void printingA() {
        System.out.println("a");
    }

}