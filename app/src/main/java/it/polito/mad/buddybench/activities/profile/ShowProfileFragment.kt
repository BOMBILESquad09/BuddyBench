package it.polito.mad.buddybench.activities.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity

@AndroidEntryPoint
class ShowProfileFragment(val context: HomeActivity): Fragment(R.layout.activity_show_profile) {
    var profile = context.profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setGUI()
    }

    private fun setGUI(){

        val thisView = requireView()

        val fullNameTv = thisView.findViewById<TextView>(R.id.fullNameView)
        fullNameTv.text = profile.fullName

        val nicknameTv = requireView().findViewById<TextView>(R.id.nickNameView)
        nicknameTv.text = profile.nickname

        val ageTv = thisView.findViewById<TextView>(R.id.ageView)
        ageTv.text = getString(R.string.age).format(profile.age)

        val locationTv = thisView.findViewById<TextView>(R.id.locationView)
        locationTv.text = profile.location

        val matchesPlayedTv = thisView.findViewById<TextView>(R.id.matchesPlayedView)
        matchesPlayedTv.text = profile.matchesPlayed.toString()

        val matchesOrganizedTv = thisView.findViewById<TextView>(R.id.matchesOrganizedView)
        matchesOrganizedTv.text = profile.matchesOrganized.toString()

        val reliabilityTv = thisView.findViewById<TextView>(R.id.reliabilityView)
        reliabilityTv.text = getString(R.string.reliabilityValue).format(profile.reliability)

        val iv = thisView.findViewById<ImageView>(R.id.imageEdit)
        resizeImageView(iv)
        try{
            iv.setImageURI(profile.imageUri)
        } catch (_: Exception){
            iv.setImageResource(R.drawable.person)
        }

        val sportContainer = thisView.findViewById<LinearLayout>(R.id.sportsContainerEdit)
        sportContainer.removeAllViews()

        // ** Populate sport cards
        profile.populateSportCards(context, sportContainer)
    }


    private  fun resizeImageView(iv: ImageView){
        return
        /*val ll = requireView().findViewById<LinearLayout>(R.id.imageContainer)
        ll.post {
            val width = ll.width
            val height = ll.height
            if (width == height) return@post
            val diameter = width.coerceAtMost(height)
            iv.layoutParams = FrameLayout.LayoutParams(diameter, diameter)
            iv.requestLayout()
        }*/

    }
}