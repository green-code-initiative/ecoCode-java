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

import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import java.util.Collections;

public class UseFetchTypeLazyRuleTest {

    @OneToMany(mappedBy = "firstEntity") // Noncompliant
    private Collection<SomeEntity> firstEntities;

    @ManyToMany(mappedBy = "firstEntity1") // Noncompliant
    private Collection<SomeEntity> firstEntities1;

    @OneToMany // Noncompliant
    private Collection<SomeEntity> secondEntities1;

    @ManyToMany // Noncompliant
    private Collection<SomeEntity> secondEntities2;

    @OneToMany(mappedBy = "thirdEntity1", fetch= FetchType.EAGER) // Noncompliant
    private Collection<SomeEntity> thirdEntities1;

    @ManyToMany(mappedBy = "thirdEntity1", fetch= FetchType.EAGER) // Noncompliant
    private Collection<SomeEntity> thirdEntities2;

    @OneToMany(fetch = FetchType.LAZY) // Compliant
    private Collection<SomeEntity> fourthEntities1;

    @ManyToMany(fetch = FetchType.LAZY) // Compliant
    private Collection<SomeEntity> fourthEntities2;

}
