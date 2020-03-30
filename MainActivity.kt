package com.example.colorpicker

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    // seekBar Steps, and min and max values
    val MIN = 0
    val MAX = 255
    val STEP = 5


    // val, var
    var textViewColor: TextView? = null
    var seekBarRed: SeekBar? = null
    var seekBarBlue: SeekBar? = null
    var seekBarGreen: SeekBar? = null
    var colorView: View? = null


    var colorGreen: Int = 0
    var colorRed: Int = 0
    var colorBlue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set click listener for button
        val button = findViewById<Button>(R.id.btnSelectColor)
        button.setOnClickListener { chooseColor() }

        //1. 3 seekbars, RGB
        seekBarRed = findViewById(R.id.redSeekBar)
        seekBarBlue = findViewById(R.id.blueSeekBar)
        seekBarGreen = findViewById(R.id.greenSeekBar)
        textViewColor = findViewById(R.id.colorTextView)
        colorView = findViewById(R.id.colorView)

        //2. listen for seekBar value changes
        seekBarRed?.setOnSeekBarChangeListener(this)
        seekBarBlue?.setOnSeekBarChangeListener(this)
        seekBarGreen?.setOnSeekBarChangeListener(this)

        // set the max value for each seekbar
        // max value

        val maxValue = MAX / STEP
        seekBarGreen?.max =maxValue
        seekBarRed?.max =maxValue
        seekBarBlue?.max =maxValue

        // set the initial text for color values
        textViewColor?.text = "Red($colorRed), Green($colorGreen), Blue($colorBlue)"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_save){
            saveColor()
            return true
        } else if (id == R.id.action_recall) {
            recallColor()
            return true
        }
        else return super.onOptionsItemSelected(item)
    }

    private fun saveColor() {
        // show dialog and save with created name
        DisplayUtils.showNameDialogBox(this) { createdColorName ->
            // get all colors
            val customColors = getColors()
            val existingColor = customColors.colors.find { color -> color.name == createdColorName }
            val nameAlreadyExists = existingColor != null

            if (nameAlreadyExists) {
                // show toast error
                showToast("Color name already exists!")
            } else {
                // create color
                val newColor = CustomColor (
                        name = createdColorName,
                        red = colorRed,
                        green = colorGreen,
                        blue = colorBlue
                )

                // add new color
                customColors.colors.add(newColor)

                // save color
                addColor(customColors)
                showToast("$createdColorName color, saved successfully")
            }
        }
    }


    private fun recallColor() {
        val customColors = getColors()
        DisplayUtils.showListDialogBox(this, customColors) { selectedColor ->
            // set the colors to be selected color
            colorRed = selectedColor.red
            colorGreen = selectedColor.green
            colorBlue = selectedColor.blue

            // set the sliders to color values
            seekBarRed?.setProgress(colorRed / STEP)
            seekBarGreen?.setProgress(colorGreen / STEP)
            seekBarBlue?.setProgress(colorBlue / STEP)

            // update user interface
            updateUI()
        }

    }

    private fun chooseColor() {
        // put the selected colors in a result bundle
        val resultIntent = Intent()
        resultIntent.putExtra(KEY_COLOR_RED, colorRed)
        resultIntent.putExtra(KEY_COLOR_BLUE, colorBlue)
        resultIntent.putExtra(KEY_COLOR_GREEN, colorGreen)

        // pass the bundle as a result to the other activity
        setResult(Activity.RESULT_OK, resultIntent)

        // close activity
        finish()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // 0 - 100
        //3. use the % lvl to get the color
        val colorInt = (progress * STEP)

        // check which seekbar this is, and update its color
        if (seekBar == seekBarRed) {
            colorRed = colorInt
        } else if(seekBar == seekBarGreen){
            colorGreen = colorInt
        }else{
            colorBlue = colorInt
        }

        // update user interface
        updateUI()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    private fun updateUI() {
        //4. use the gotten color on txt view
        textViewColor?.text = "Red($colorRed), Green($colorGreen), Blue($colorBlue)"
        val color = Color.rgb(colorRed, colorGreen, colorBlue)
        colorView?.setBackgroundColor(color)
    }

    private fun getColors(): CustomColors {
        // get the shared preference xml file
        val sharedPref = getSharedPreferences(SHARED_PREF_FILE, Activity.MODE_PRIVATE)

        // get the list of colors as a string
        val jsonString = sharedPref.getString(KEY_COLORS_NAMES, "")
        val exists = jsonString?.isNotEmpty() ?: false
        if (!exists) {
            return CustomColors(ArrayList())
        } else {
            val json = jsonString!!
            val gson = Gson()
            val list = gson.fromJson(json, CustomColors::class.java)
            return list
        }
    }

    private fun addColor(customColors: CustomColors) {
        // converst the custom colors json string
        // create gson parser
        val gson = Gson()
        val jsonString = gson.toJson(customColors) // { "colors": [{"red": 123, "green": 34, "blue": 123}] }

        // get shared preferences
        val sharedPref = getSharedPreferences(SHARED_PREF_FILE, Activity.MODE_PRIVATE)
        val editor = sharedPref.edit()
        // set the new set of colors
        editor.putString(KEY_COLORS_NAMES, jsonString)
        // save the file
        editor.commit()
    }

    private fun showToast(message: String) {
        Toast
                .makeText(this, message, Toast.LENGTH_LONG)
                .show()
    }
}

// result keys
private val KEY_COLOR_RED = "red_color_key"
private val KEY_COLOR_GREEN = "green_color_key"
private val KEY_COLOR_BLUE = "blue_color_key"



private val SHARED_PREF_FILE = "color.picker.colors"
private val KEY_COLORS_NAMES = "key_colors_names"
