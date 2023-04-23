package it.polito.mad.buddybench.activities.findcourt

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity

class SearchFragment(val parent: FindCourtFragment): Fragment(R.layout.blank) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = view.findViewById<Button>(R.id.showSelection)

        b.setOnClickListener{
            parent.fragmentManager.switchFragment(States.SPORTS_SELECTION)
        }
        parent.viewModel.selectedSport.observe(viewLifecycleOwner){
            b.text = it.name
        }
    }
}