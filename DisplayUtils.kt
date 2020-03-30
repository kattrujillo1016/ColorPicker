package com.example.colorpicker

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText


object DisplayUtils {

    fun showListDialogBox(context: Context, customColors: CustomColors, callback: (CustomColor) -> Unit) {
        // create a string array ["red", "magenta", "violet"]
        val nameArray: Array<String> = customColors.colors.map { color -> color.name }.toTypedArray()
        AlertDialog.Builder(context)
            .setTitle("Choose a color")
            .setCancelable(false)
            .setItems(nameArray) { dialog, index ->
                val color = customColors.colors[index]
                callback.invoke(color)
            }
            .show()
    }

    fun showNameDialogBox(context: Context, callback: (String) -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.content_color_name, null)
        val nameEditText: EditText  = view.findViewById(R.id.colorName)

        // create the dialog
        AlertDialog.Builder(context)
            .setTitle("Type in the color name")
            .setView(view)
            .setCancelable(false)
            .setPositiveButton("Save") { dialog, _ ->
                val name = nameEditText.text.toString()
                val isValid = name.isNotBlank()

                if(!isValid) {
                    AlertDialog.Builder(context)
                        .setTitle("Invalid color name")
                        .setMessage("Please enter a valid color name")
                        .show()
                } else {
                    // trigger call back with color name
                    callback.invoke(name)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }
}