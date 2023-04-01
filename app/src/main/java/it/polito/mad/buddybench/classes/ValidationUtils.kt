package it.polito.mad.buddybench.classes

import android.content.res.ColorStateList
import android.content.res.Resources
import android.widget.EditText
import it.polito.mad.buddybench.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ValidationUtils {
    companion object{
        fun validateString(string: String?): Boolean{
            return string != null && string.trim().isNotEmpty()
        }

        fun validateLocalDate(string: String?): Boolean{
            try{
                println(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(string))
            } catch (_: java.lang.Exception){
                return false
            }
            return true
        }

        fun changeColor(ev: EditText, string: String, resources: Resources){
            if(!validateString(string)){
                ev.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.error, null))
            }
            else{
                ev.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.md_theme_dark_background,null))
            }
        }

        fun changeColorDate(ev: EditText, string:String, resources: Resources){
            if(!validateLocalDate(string)){
                ev.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.error, null))
            }
            else{
                ev.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.md_theme_dark_background,null))
            }
        }
    }
}