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

/**
 * Compliant
 */
public class DetectUnoptimizedImageFormatCompliant {

    public String testImage(String image) {
        return "path/to/" + image;
    }

    public String testImageFormat2() {


        String img_svg = "test/image.svg";                    // Compliant

        String image_format = testImage("image.svg");         // Compliant

        String image_svg_html = "<html><svg width=\"100\" height=\"100\">" +  // Compliant
                "<circle cx=\"50\" cy=\"50\" r=\"40\" stroke=\"green\" stroke-width=\"4\" fill=\"yellow\" />" +
                "</svg></html>";

        return "<html><img src=\"xx/xx/image.svg\" >"  // Compliant
                + "</html>";
    }
}
