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
 * Not compliant
 */
public class DetectUnoptimizedImageFormat {

    public String testImage(String image) {
        return "path/to/" + image;
    }

    public String testImageFormat2() {

        String img_bmp = "test/image.bmp"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_ico = "image.ico"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_tiff = "test/path/to/image.tiff"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_webp = "test/path/to/" + "image.webp"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_jpg = "image.jpg"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_jpeg = "image.jpeg"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_jfif = "image.jfif"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_pjpeg = "image.pjpeg"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_pjp = "image.pjp"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_gif = "image.gif"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_avif = "image.avif"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
        String img_apng = "image.apng"; // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}

        String image_format = testImage("image.jpg"); // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}

        return "<html><img src=\"xx/xx/image.bmp\" >" // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.ico\" >"     // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.tiff\" >"    // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.webp\" >"    // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.png\" >"     // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.jpg\" >"     // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.jpeg\" >"    // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.jfif\" >"    // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.pjpeg\" >"   // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.pjp\" >"     // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.gif\" >"     // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.avif\" >"    // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "<img src=\"xx/xx/image.apng\" >"    // Noncompliant {{If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.}}
                + "</html>";
    }
}