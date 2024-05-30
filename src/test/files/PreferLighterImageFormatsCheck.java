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

import java.util.logging.Logger;

class PreferLighterImageFormatsCheck {

    private static final Logger LOGGER = Logger.getLogger(PreferLighterImageFormatsCheck.class.getName());

    public PreferLighterImageFormatsCheck(PreferLighterImageFormatsCheck checker) {
    }


    public void testDirectAssignments() {
        String imagePath1 = "images/photo.jpg"; // Noncompliant
        String imagePath2 = "images/graphic.png"; // Noncompliant
        String imagePath3 = "images/photo.webp"; // Compliant
        String imagePath4 = "images/graphic.avif"; // Compliant
    }

    public void testPathsInArray() {
        String[] imagePaths = {
                "assets/image1.jpg", // Noncompliant
                "assets/image2.png", // Noncompliant
                "assets/image3.webp", // Compliant
                "assets/image4.avif" // Compliant
        };

        for (String imagePath : imagePaths) {
            LOGGER.info("Image path: " + imagePath);
        }
    }

    public void testPathsInMethods() {
        logImagePath("icons/icon1.jpg"); // Noncompliant
        logImagePath("icons/icon2.png"); // Noncompliant
        logImagePath("icons/icon3.webp"); // Compliant
        logImagePath("icons/icon4.avif"); // Compliant
    }
}
