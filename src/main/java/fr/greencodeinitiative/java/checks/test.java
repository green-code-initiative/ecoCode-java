package fr.greencodeinitiative.java.checks;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class test {

    public void reflectionMethodsExample() throws Exception {
        Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
        Method method = clazz.getMethod("someMethod");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        method.invoke(instance);

        Field field = clazz.getDeclaredField("someField");
        field.setAccessible(true);
        Object value = field.get(instance);
        field.set(instance, value);
    }

    public void stringManipulationExample() { // Noncompliant
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += "example";
        }

        String str = "This is a test";
        str = str.replace("test", "sample");
        str = str.substring(5, 10);
        boolean matches = str.matches("\\d+");
        String[] parts = str.split(" ");
    }

    public void synchronizedCollectionsExample() { // Noncompliant
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        list.add(1);
        list.add(2);
        list.get(0);

        Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
        map.put("key1", "value1");
        map.get("key1");
        map.remove("key1");

        Set<String> set = Collections.synchronizedSet(new HashSet<>());
        set.add("item1");
        set.remove("item1");
    }

    public void fileIOExample() throws IOException { // Noncompliant
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            int data = fis.read();
            while (data != -1) {
                data = fis.read();
            }
        }

        try (FileOutputStream fos = new FileOutputStream("file.txt")) {
            fos.write("Hello, World!".getBytes());
        }

        Path path = Paths.get("file.txt");
        byte[] fileBytes = Files.readAllBytes(path);
        Files.write(path, "Hello, World!".getBytes());
    }

    public void networkIOExample() throws IOException { // Noncompliant
        try (Socket socket = new Socket("localhost", 8080)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            os.write("Hello".getBytes());
            is.read();
        }

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            Socket clientSocket = serverSocket.accept();
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();
            os.write("Hello".getBytes());
            is.read();
        }

        URL url = new URL("http://example.com");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream is = connection.getInputStream();
        OutputStream os = connection.getOutputStream();
        os.write("Hello".getBytes());
        is.read();
    }

    public void jniExample() { // Noncompliant
        System.loadLibrary("nativeLib");
    }

    public void combinedExample() throws Exception { // Noncompliant
        // Reflection
        Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
        Method method = clazz.getMethod("someMethod");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        method.invoke(instance);

        // String manipulation
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += "example";
        }

        // File IO
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            int data = fis.read();
            while (data != -1) {
                data = fis.read();
            }
        }

        // Network IO
        try (Socket socket = new Socket("localhost", 8080)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            os.write("Hello".getBytes());
            is.read();
        }
    }

    public void loopWithCostlyMethodExample() { // Noncompliant
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    public void databaseAccessExample() { // Noncompliant
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "password");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM my_table");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("column_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void combinedCostlyMethodsExample() { // Noncompliant
        // String manipulation
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += "example";
        }

        // Reflection
        try {
            Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
            Method method = clazz.getMethod("someMethod");
            Object instance = clazz.getDeclaredConstructor().newInstance();
            method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // File IO
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            int data = fis.read();
            while (data != -1) {
                data = fis.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /// BREAK

    // Reflection Methods Example
    public void reflectionMethodsExample_ok() throws Exception {
        Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
        Method method = clazz.getMethod("someMethod");
    }

    public void reflectionMethodsExample_ko() throws Exception { // Noncompliant
        Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
        Method method = clazz.getMethod("someMethod");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        method.invoke(instance);

        Field field = clazz.getDeclaredField("someField");
        field.setAccessible(true);
        Object value = field.get(instance);
        field.set(instance, value);
    }

    // String Manipulation Example
    public void stringManipulationExample_ok() {
        String result = "";
        result += "example";
    }

    public void stringManipulationExample_ko() { // Noncompliant
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += "example";
        }

        String str = "This is a test";
        str = str.replace("test", "sample");
        str = str.substring(5, 10);
        boolean matches = str.matches("\\d+");
        String[] parts = str.split(" ");
    }

    // Synchronized Collections Example
    public void synchronizedCollectionsExample_ok() {
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        list.add(1);

        Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
        map.put("key1", "value1");
    }

    public void synchronizedCollectionsExample_ko() { // Noncompliant
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        list.add(1);
        list.add(2);
        list.get(0);

        Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
        map.put("key1", "value1");
        map.get("key1");
        map.remove("key1");

        Set<String> set = Collections.synchronizedSet(new HashSet<>());
        set.add("item1");
        set.remove("item1");
    }

    // File IO Example
    public void fileIOExample_ok() throws IOException {
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            fis.read();
        }
    }

    public void fileIOExample_ko() throws IOException { // Noncompliant
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            int data = fis.read();
            while (data != -1) {
                data = fis.read();
            }
        }

        try (FileOutputStream fos = new FileOutputStream("file.txt")) {
            fos.write("Hello, World!".getBytes());
        }

        Path path = Paths.get("file.txt");
        byte[] fileBytes = Files.readAllBytes(path);
        Files.write(path, "Hello, World!".getBytes());
    }

    // Network IO Example
    public void networkIOExample_ok() throws IOException {
        try (Socket socket = new Socket("localhost", 8080)) {
            socket.getInputStream();
        }
    }

    public void networkIOExample_ko() throws IOException { // Noncompliant
        try (Socket socket = new Socket("localhost", 8080)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            os.write("Hello".getBytes());
            is.read();
        }

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            Socket clientSocket = serverSocket.accept();
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();
            os.write("Hello".getBytes());
            is.read();
        }

        URL url = new URL("http://example.com");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream is = connection.getInputStream();
        OutputStream os = connection.getOutputStream();
        os.write("Hello".getBytes());
        is.read();
    }

    // JNI Example
    public void jniExample_ok() {
        // Less than 5 points of JNI calls
    }

    public void jniExample_ko() { // Noncompliant
        System.loadLibrary("nativeLib");
    }

    // Combined Example
    public void combinedExample_ok() throws Exception {
        // Reflection
        Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
        Method method = clazz.getMethod("someMethod");

        // String manipulation
        String result = "";
        for (int i = 0; i < 2; i++) {
            result += "example";
        }

        // File IO
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            fis.read();
        }

        // Network IO
        try (Socket socket = new Socket("localhost", 8080)) {
            socket.getInputStream();
        }
    }

    public void combinedExample_ko() throws Exception { // Noncompliant
        // Reflection
        Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
        Method method = clazz.getMethod("someMethod");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        method.invoke(instance);

        // String manipulation
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += "example";
        }

        // File IO
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            int data = fis.read();
            while (data != -1) {
                data = fis.read();
            }
        }

        // Network IO
        try (Socket socket = new Socket("localhost", 8080)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            os.write("Hello".getBytes());
            is.read();
        }
    }

    // Loop with Costly Method Example
    public void loopWithCostlyMethodExample_ok() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
    }

    public void loopWithCostlyMethodExample_ko() { // Noncompliant
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    // Database Access Example
    public void databaseAccessExample_ok() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "password");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM my_table");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void databaseAccessExample_ko() { // Noncompliant
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "password");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM my_table");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("column_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Combined Costly Methods Example
    public void combinedCostlyMethodsExample_ok() {
        // String manipulation
        String result = "";
        for (int i = 0; i < 2; i++) {
            result += "example";
        }

        // Reflection
        try {
            Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
            Method method = clazz.getMethod("someMethod");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void combinedCostlyMethodsExample_ko() { // Noncompliant
        // String manipulation
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += "example";
        }

        // Reflection
        try {
            Class<?> clazz = Class.forName("com.yourcompany.SomeClass");
            Method method = clazz.getMethod("someMethod");
            Object instance = clazz.getDeclaredConstructor().newInstance();
            method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // File IO
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            int data = fis.read();
            while (data != -1) {
                data = fis.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

