package it.polito.mad.buddybench.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import org.json.JSONObject
import org.w3c.dom.Text

class EditProfileActivity : AppCompatActivity() {
    lateinit var profile: Profile
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        profile = Profile.fromJSON(JSONObject(intent.getStringExtra("profile")!!))
        val fullNameEdit = findViewById<EditText>(R.id.fullNameEdit)
        fullNameEdit.setText(profile.fullName)

        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        nicknameEdit.setText(profile.nickname)

        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        localityEdit.setText(profile.location)

    }
}