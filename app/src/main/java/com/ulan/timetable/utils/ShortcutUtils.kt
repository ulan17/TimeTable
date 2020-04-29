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

package com.ulan.timetable.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.ulan.timetable.R
import com.ulan.timetable.activities.HomeworksActivity
import com.ulan.timetable.activities.NotesActivity


@RequiresApi(25)
class ShortcutUtils {

    companion object {
        fun createShortcuts(context: Context) {

            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            val shortcutList = mutableListOf<ShortcutInfo>()
            shortcutList.add(createAddHomeworkShortcut(context))
            shortcutList.add(createNotesShortcut(context))

            shortcutManager!!.dynamicShortcuts = shortcutList
        }


        private const val size = 256
        private const val padding = 65
        private fun createShortcut(context: Context, id: String, shortLabel: String, iconId: Int, intent: Intent): ShortcutInfo {
            val icon = ContextCompat.getDrawable(context, iconId)
            icon?.setTint(Color.WHITE)

            val background = ContextCompat.getDrawable(context, R.drawable.shortcuts_background)
            val combined = LayerDrawable(arrayOf(background, icon))
            combined.setLayerInset(1, padding, padding, padding, padding)

            val combinedIcon = if (Build.VERSION.SDK_INT > 25) Icon.createWithAdaptiveBitmap(combined.toBitmap(size, size)) else Icon.createWithBitmap(combined.toBitmap(size, size))

            return ShortcutInfo.Builder(context, id)
                    .setShortLabel(shortLabel)
                    .setIcon(combinedIcon)
                    .setIntent(intent)
                    .build()
        }

        private fun createAddHomeworkShortcut(context: Context): ShortcutInfo {
            return createShortcut(context, "add_homework", context.getString(R.string.add_homework), R.drawable.ic_book_black_24dp, Intent(context, HomeworksActivity::class.java).setAction(HomeworksActivity.ACTION_ADD_HOMEWORK))
        }

        private fun createNotesShortcut(context: Context): ShortcutInfo {
            return createShortcut(context, "open_notes", context.getString(R.string.notes_activity_title), R.drawable.ic_event_note_black_24dp, Intent(context, NotesActivity::class.java).setAction(Intent.ACTION_VIEW))
        }
    }
}