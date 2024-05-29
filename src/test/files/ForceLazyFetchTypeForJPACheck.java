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


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity
public class JpaEntity {

    @OneToMany
    private List<ChildEntity> children; // Noncompliant

    @ManyToMany
    private Set<AnotherEntity> others; // Noncompliant

    @OneToOne
    private OneEntity one; // Noncompliant

    @ManyToOne
    private ManyEntity many; // Noncompliant

    @OneToMany(fetch = FetchType.EAGER)
    private List<SomeEntity> someEntitiesEager; // Noncompliant

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<AnotherEntity> othersEager; // Noncompliant

    @OneToOne(fetch = FetchType.EAGER)
    private OneEntity oneEager; // Noncompliant

    @ManyToOne(fetch = FetchType.EAGER)
    private ManyEntity manyEager; // Noncompliant

    @OneToMany(fetch = FetchType.LAZY)
    private List<SomeEntity> someEntitiesLazy;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<AnotherEntity> othersLazy;

    @OneToOne(fetch = FetchType.LAZY)
    private OneEntity oneLazy;

    @ManyToOne(fetch = FetchType.LAZY)
    private ManyEntity manyLazy;

}