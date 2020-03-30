
package com.example.colorpicker

data class CustomColor (
   val name: String,
   val red: Int,
   val green: Int,
   val blue: Int
)

data class CustomColors(
   val colors: MutableList<CustomColor>
)
