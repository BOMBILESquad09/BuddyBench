package it.polito.mad.buddybench.activities.findcourt

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.entities.User
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.viewmodels.SportsSelectionViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel

@AndroidEntryPoint
class FindCourtFragment(val context: HomeActivity): Fragment(R.layout.find_court) {
    val fragmentManager = FindCourtFragmentManager(this)
    val viewModel by viewModels<SportsSelectionViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentManager.setup()
    }

}