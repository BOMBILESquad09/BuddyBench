package it.polito.mad.buddybench.activities.profile

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.viewmodels.ImageViewModel

@AndroidEntryPoint
class ShowProfileFragment(val context: HomeActivity): Fragment(R.layout.show_profile) {

    lateinit var profile: Profile
    private val imageViewModel by activityViewModels<ImageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        context.userViewModel.user.observe(this){
            if (it != null){


                profile = it

                setGUI()
            }
        }
    }




    private fun setGUI(){

        val thisView = requireView()

        val fullNameTv = thisView.findViewById<TextView>(R.id.fullname_tv)
        fullNameTv.text = profile.fullName

        val nicknameTv = requireView().findViewById<TextView>(R.id.nickname_tv)
        nicknameTv.text = "@${profile.nickname}"

        val ageTv = thisView.findViewById<TextView>(R.id.age_tv)
        ageTv.text = getString(R.string.age).format(profile.age)

        val locationTv = thisView.findViewById<TextView>(R.id.location_tv)
        locationTv.text = profile.location

        val matchesPlayedTv = thisView.findViewById<TextView>(R.id.games_played)
        matchesPlayedTv.text = profile.getMatchesPlayed().toString()

        val matchesOrganizedTv = thisView.findViewById<TextView>(R.id.games_organized)
        matchesOrganizedTv.text = profile.getMatchesOrganized().toString()

        val reliabilityTv = thisView.findViewById<TextView>(R.id.reliability)
        reliabilityTv.text = getString(R.string.reliabilityValue).format(profile.reliability)

        val iv = thisView.findViewById<ImageView>(R.id.profile_image)
        //resizeImageView(iv)
        try{
            imageViewModel.getUserImage(profile.email,{iv.setImageResource(R.drawable.person)}){

                Glide.with(this)
                    .load(it)
                    .skipMemoryCache(true)
                    .into(iv)
            }
        } catch (_: Exception){
            iv.setImageResource(R.drawable.person)
        }

        val sportsRecyclerView = thisView.findViewById<RecyclerView>(R.id.sports_container)
        sportsRecyclerView.layoutManager = LinearLayoutManager(context).let {
            it.orientation = RecyclerView.HORIZONTAL
            it
        }

        context.userViewModel.setSports(profile.sports)

        sportsRecyclerView.adapter = SportsAdapter(context.userViewModel.sports, false)
        context.userViewModel.sports.observe(this){
            if (it == null) return@observe
        }
        if(profile.sports.none { it.skill != Skills.NULL }){
            thisView.findViewById<TextView>(R.id.empty_sports).visibility= View.VISIBLE
        } else{
            thisView.findViewById<TextView>(R.id.empty_sports).visibility= View.GONE
        }



        // ** Populate sport cards
        //profile.populateSportCards(context, sportContainer)

    }


    private  fun resizeImageView(iv: ImageView){

        val ll = requireView().findViewById<LinearLayout>(R.id.imageContainer)
        ll.post {
            val width = ll.width
            val height = ll.height
            if (width == height) return@post
            val diameter = width.coerceAtMost(height)
            iv.layoutParams = FrameLayout.LayoutParams(diameter, diameter)
            iv.requestLayout()
        }
    }



}