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

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class AvoidEnergyConsumingMethodsCheck {

    // Reflection Methods Example
    public void reflectionMethodsTest_ok() throws Exception {
        Class<?> clazz = Class.forName("com.sonar.SomeClass");
        Method method = clazz.getMethod("someMethod");
    }

    public void reflectionMethodsTest_ko() throws Exception { // Noncompliant
        Class<?> clazz = Class.forName("com.sonar.SomeClass");
        Method method = clazz.getMethod("someMethod");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        method.invoke(instance);

        Field field = clazz.getDeclaredField("someField");
        field.setAccessible(true);
        Object value = field.get(instance);
        field.set(instance, value);
        field.set(instance, value);
    }

    // String Manipulation Example
    public void stringManipulationTest_ok() {
        String result = "";
        result += "example";
    }

    public void stringManipulationTest_ko() { // Noncompliant
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += "example";
        }

        String str = "This is a test";
        str = str.replace("test", "sample");
        str = str.substring(5, 10);
        boolean matches = str.matches("\\d+");
        String[] parts = str.split(" ");
        String[] parts = str.split(" ");
        String[] parts = str.split(" ");
    }

    // Synchronized Collections Example
    public void synchronizedCollectionsTest_ok() {
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        list.add(1);

        Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
        map.put("key1", "value1");
    }

    public void synchronizedCollectionsTest_ko() { // Noncompliant
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        list.add(1);
        list.add(2);
        list.get(0);

        Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
        map.put("key1", "value1");
        map.get("key1");
        map.remove("key1");

        Set<String> set = Collections.synchronizedSet(new HashSet<>());
        Set<String> set = Collections.synchronizedSet(new HashSet<>());
        Set<String> set = Collections.synchronizedSet(new HashSet<>());
        Set<String> set = Collections.synchronizedSet(new HashSet<>());
        set.add("item1");
        set.remove("item1");
    }

    // File IO Example
    public void fileIOTest_ok() throws IOException {
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            fis.read();
        }
    }

    public void fileIOTest_ko() throws IOException { // Noncompliant
        try (FileInputStream fis = new FileInputStream("file.txt")) {
            int data = fis.read();
            while (data != -1) {
                data = fis.read();
            }
        }

        try (FileOutputStream fos = new FileOutputStream("file.txt")) {
            fos.write("Hello, World!".getBytes());
            fos.write("Hello, World!".getBytes());
            fos.write("Hello, World!".getBytes());
        }

        Path path = Paths.get("file.txt");
        Path path = Paths.get("file.txt");
        byte[] fileBytes = Files.readAllBytes(path);
        Files.write(path, "Hello, World!".getBytes());
    }

    // Network IO Example
    public void networkIOTest_ok() throws IOException {
        try (Socket socket = new Socket("localhost", 8080)) {
            socket.getInputStream();
        }
    }

    public void networkIOTest_ko() throws IOException { // Noncompliant
        try (Socket socket = new Socket("localhost", 8080)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
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
        connection.connect();
        InputStream is = connection.getInputStream();
        OutputStream os = connection.getOutputStream();
        os.write("Hello".getBytes());
        is.read();
    }

    // JNI Example
    public void jniTest_ok() {
        System.loadLibrary("nativeLib");
    }

    public void jniTest_ko() { // Noncompliant
        System.loadLibrary("nativeLib");
        System.loadLibrary("nativeLib");
        System.loadLibrary("nativeLib");
        System.loadLibrary("nativeLib");
        System.loadLibrary("nativeLib");
        System.loadLibrary("nativeLib");
    }

    // Combined Example
    public void combinedTest_ok() throws Exception {
        // Reflection
        Class<?> clazz = Class.forName("com.sonar.SomeClass");
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

    public void combinedTest_ko() throws Exception { // Noncompliant
        // Reflection
        Class<?> clazz = Class.forName("com.sonar.SomeClass");
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

    // Combined Costly Methods Example
    public void combinedCostlyMethodsTest_ok() {
        // String manipulation
        String result = "";
        for (int i = 0; i < 2; i++) {
            result += "example";
        }

        // Reflection
        try {
            Class<?> clazz = Class.forName("com.sonar.SomeClass");
            Method method = clazz.getMethod("someMethod");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void combinedCostlyMethodsTest_ko() { // Noncompliant
        // String manipulation
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += "example";
        }

        // Reflection
        try {
            Class<?> clazz1 = Class.forName("com.sonar.SomeClass");
            Class<?> clazz = Class.forName("com.sonar.SomeClass");
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