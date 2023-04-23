package it.polito.mad.buddybench.activities.findcourt.SportSelection

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.findcourt.FindCourtFragment
import it.polito.mad.buddybench.activities.findcourt.States
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.SportsSelectionViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SportsSelectionFragment(val parent: FindCourtFragment): Fragment(R.layout.sports_selection){

    private val context = parent.context
    lateinit var sportsRecyclerView: RecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sportsRecyclerView = view.findViewById(R.id.sports_selection)
        sportsRecyclerView.layoutManager = LinearLayoutManager(context)
        parent.viewModel.getAll().observe(viewLifecycleOwner){
            sportsRecyclerView.adapter = SportsSelectionAdapter(it) {sport->
                parent.viewModel.selectedSport.value = sport
                parent.fragmentManager.switchFragment(States.SEARCH)
            }
        }
    }
}