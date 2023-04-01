package it.polito.mad.buddybench.classes

import android.content.res.ColorStateList
import android.content.res.Resources
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Patterns
import android.widget.EditText
import androidx.compose.ui.text.input.KeyboardType.Companion.Email
import androidx.core.net.MailTo
import it.polito.mad.buddybench.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
class ValidationUtils {
    companion object{
        fun validateString(string: String?): Boolean{
            return string != null && string.trim().isNotEmpty()
        }

        fun validateEmail(string: String?): Boolean{
            if(!validateString(string)) return  false
            //maybe this pattern
            //the following instructions returns true for invalid email like name@domain.i should be false.
            //at least two characters after the last dot
            return Patterns.EMAIL_ADDRESS.matcher(string).matches();
        }
        fun validateLocalDate(string: String?): Boolean{
            try{
                println(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(string))
            } catch (_: java.lang.Exception){
                return false
            }
            return true
        }

        fun changeColor(ev: EditText, success: Boolean, resources: Resources){
            if(!success){
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