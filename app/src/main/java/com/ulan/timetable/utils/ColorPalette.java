/*
 * Copyright (c) 2020 Felix Hollederer
 *     This file is part of GymWenApp.
 *
 *     GymWenApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GymWenApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with GymWenApp.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.ulan.timetable.utils;

import android.graphics.Color;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ColorPalette {

    public static final int[] PRIMARY_COLORS =
            new int[]{
                    Color.parseColor("#F44336"),
                    Color.parseColor("#E91E63"),
                    Color.parseColor("#9C27B0"),
                    Color.parseColor("#673AB7"),
                    Color.parseColor("#3F51B5"),
                    Color.parseColor("#2196F3"),
                    Color.parseColor("#03A9F4"),
                    Color.parseColor("#00BCD4"),
                    Color.parseColor("#009688"),
                    Color.parseColor("#4CAF50"),
                    Color.parseColor("#8BC34A"),
                    Color.parseColor("#CDDC39"),
                    Color.parseColor("#FFEB3B"),
                    Color.parseColor("#FFC107"),
                    Color.parseColor("#FF9800"),
                    Color.parseColor("#FF5722"),
                    Color.parseColor("#795548"),
                    Color.parseColor("#9E9E9E"),
                    Color.parseColor("#607D8B"),
                    Color.parseColor("#000000"),
                    Color.parseColor("#FFFFFF"),
                    Color.parseColor("#FF0000"),
                    Color.parseColor("#0000FF"),
                    Color.parseColor("#00FF00"),
                    Color.parseColor("#FF00FF"),
                    Color.parseColor("#00FFFF"),
                    Color.parseColor("#FFFF00")
            };

    public static int pickTextColorBasedOnBgColorSimple(int bgColor, int lightColor, int darkColor) {
        String color = String.format("#%06X", (0xFFFFFF & bgColor));
        color = color.substring(1, 7);
        int r = Integer.parseInt(color.substring(0, 2), 16); // hexToR
        int g = Integer.parseInt(color.substring(2, 4), 16); // hexToG
        int b = Integer.parseInt(color.substring(4, 6), 16); // hexToB
        return (((r * 0.299) + (g * 0.587) + (b * 0.114)) > 186) ?
                darkColor : lightColor;
    }
}