package it.polito.mad.buddybench.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.EditProfileActivity
import it.polito.mad.buddybench.activities.ShowProfileActivity
import nl.joery.animatedbottombar.AnimatedBottomBar

class BottomBar(val context: AppCompatActivity) {

    fun setup(){
        val bottomBar = context.findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                val intent = when(newTab.title.lowercase()){
                    "profile_" -> Intent(context, ShowProfileActivity::class.java)
                    else -> { null}
                }
                if (intent != null){
                    context.startActivity(intent)
                }
            }

        })
    }



}