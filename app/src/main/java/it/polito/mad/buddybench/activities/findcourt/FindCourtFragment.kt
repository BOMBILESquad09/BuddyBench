package it.polito.mad.buddybench.activities.findcourt

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity


@AndroidEntryPoint
class FindCourtFragment(val context: HomeActivity): Fragment(R.layout.find_court) {
    val fragmentManager = FindCourtFragmentManager(this)
    val viewModel = context.findCourtViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentManager.setup()
    }

}