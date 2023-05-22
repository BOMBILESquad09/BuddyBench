package it.polito.mad.buddybench.activities.friends

import android.app.Activity
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.activities.profile.ShowProfileFragment
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FriendProfileBinding
import it.polito.mad.buddybench.viewmodels.UserViewModel
import org.json.JSONObject

@AndroidEntryPoint
class FriendProfileActivity : AppCompatActivity() {

    private lateinit var binding: FriendProfileBinding
    val userViewModel by viewModels<UserViewModel>()
    val bundle = bundleOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FriendProfileBinding.inflate(layoutInflater)
        val profile = Profile.fromJSON(JSONObject(intent.getStringExtra("profile")))
        bundle.putString("oldProfile", profile.toJSON().toString())

        val showProfileFragment = ShowProfileFragment(
            seeProfile = true,
            friendProfile = profile
        )
        this.supportFragmentManager.beginTransaction()
            .add(binding.friendFragmentContainer.id, showProfileFragment)
            .commit()
        setContentView(binding.root)

        userViewModel.getUser(profile.email).observe(this) {
            if(it == null)
                return@observe
            showProfileFragment.profile = it
            showProfileFragment.setGUI()
        }
        onBackPressedDispatcher.addCallback {
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }





}