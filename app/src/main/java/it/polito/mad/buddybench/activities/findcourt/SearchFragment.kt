package it.polito.mad.buddybench.activities.findcourt

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.findcourt.SportSelection.CourtSearchAdapterAdapter
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.viewmodels.CourtViewModel

@AndroidEntryPoint
class SearchFragment(val parent: FindCourtFragment): Fragment(R.layout.activity_search_court) {

    private val courtViewModel by viewModels<CourtViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        val b = view.findViewById<ImageButton>(R.id.change_sport_button)
        b.setOnClickListener{
            parent.fragmentManager.switchFragment(States.SPORTS_SELECTION)
        }
        parent.viewModel.selectedSport.observe(viewLifecycleOwner){
            val l = courtViewModel.getCourtsBySport(it)
            recyclerView?.adapter = CourtSearchAdapterAdapter(l)
            recyclerView?.layoutManager = LinearLayoutManager(view.context)
        }

        super.onViewCreated(view, savedInstanceState)
    }



}