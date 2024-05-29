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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Arrays;
class NoFunctionCallWhenDeclaringForLoop {
    NoFunctionCallWhenDeclaringForLoop(NoFunctionCallWhenDeclaringForLoop mc) {
    }

    public int getMyValue() {
        return 6;
    }

    public int incrementeMyValue(int i) {
        return i + 100;
    }

    public void test1() {
        for (int i = 0; i < 20; i++) {
            System.out.println(i);
            boolean b = this.getMyValue() > 6;
        }
    }

    public void test2() {
        String[] cars = {"Volvo", "BMW", "Ford", "Mazda"};
        for (String i : cars) {
            System.out.println(i);
        }

    }

    // compliant, the function is called only once in the initialization so it's not a performance issue
    public void test3() {
        for (int i = getMyValue(); i < 20; i++) {
            System.out.println(i);
            boolean b = getMyValue() > 6;
        }
    }

    public void test4() {
        for (int i = 0; i < getMyValue(); i++) {  // Noncompliant {{Do not call a function when declaring a for-type loop}}
            System.out.println(i);
            boolean b = getMyValue() > 6;
        }
    }

    public void test5() {
        for (int i = 0; i < getMyValue(); incrementeMyValue(i)) {  // Noncompliant {{Do not call a function when declaring a for-type loop}}
            System.out.println(i);
            boolean b = getMyValue() > 6;
        }
    }

    public void test6() {
        for (int i = getMyValue(); i < getMyValue(); i++) { // Noncompliant {{Do not call a function when declaring a for-type loop}}
            System.out.println(i);
            boolean b = getMyValue() > 6;
        }
    }

    // compliant, iterators are allowed to be called in a for loop
    public void test7() {
        List<String> joursSemaine = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");
        
        String jour;
        // iterator is allowed
        for (Iterator<String> iterator = joursSemaine.iterator(); iterator.hasNext(); jour = iterator.next()) {
            System.out.println(jour);
        }

        // subclass of iterator is allowed
        for (ListIterator<String> iterator = joursSemaine.listIterator(); iterator.hasNext(); jour = iterator.next()) {
            System.out.println(jour);
        }

        // iterator called in an indirect way is allowed
        for (OtherClassWithIterator otherClass = new OtherClassWithIterator(joursSemaine); otherClass.iterator.hasNext(); jour = otherClass.iterator.next()) {
            System.out.println(jour);
        }
        // but using a method that returns an iterator causes an issue
        for (OtherClassWithIterator otherClass = new OtherClassWithIterator(joursSemaine); otherClass.getIterator().hasNext(); jour = otherClass.getIterator().next()) {  // Noncompliant {{Do not call a function when declaring a for-type loop}}
            System.out.println(jour);
        }

    }

}

class OtherClassWithIterator {
    public Iterator<String> iterator;

    public OtherClassWithIterator(Iterator<String> iterator){
        this.iterator = iterator;
    }

    public Iterator getIterator(){
        return iterator;
    }
}