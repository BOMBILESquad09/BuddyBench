package it.polito.mad.buddybench.activities.friends

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.activities.profile.ShowProfileFragment
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FriendProfileBinding
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.UserViewModel
import org.json.JSONObject

@AndroidEntryPoint
class FriendProfileActivity : AppCompatActivity() {

    private lateinit var binding: FriendProfileBinding
    val userViewModel by viewModels<UserViewModel>()
    val bundle = bundleOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel.onFailure = {
            Utils.openNetworkProblemDialog(this)
        }
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
        Utils.closeProgressDialog()
        Utils.openProgressDialog(this)

        userViewModel.getUser(profile.email).observe(this) {
            if(it == null)
                return@observe
            Utils.closeProgressDialog()
            println("-sdadfsfsfsdfsdf")
            println(it.email)
            println(it.isRequesting)
            println("-sdadfsfsfsdfsdf")

            showProfileFragment.profile = it
            showProfileFragment.setGUI()
        }




        onBackPressedDispatcher.addCallback {
            val intent = Intent()
            bundle.putString("profile", showProfileFragment.profile.toJSON().toString())
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }





}