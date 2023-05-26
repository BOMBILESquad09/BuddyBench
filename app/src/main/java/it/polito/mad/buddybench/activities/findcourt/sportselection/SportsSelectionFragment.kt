package it.polito.mad.buddybench.activities.findcourt.sportselection

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.findcourt.FindCourtFragment
import it.polito.mad.buddybench.activities.findcourt.SearchFragment
import it.polito.mad.buddybench.activities.findcourt.States

@AndroidEntryPoint
class SportsSelectionFragment(val parent: FindCourtFragment): Fragment(R.layout.sports_selection){

    private val context = parent.context
    private lateinit var sportsRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sportsRecyclerView = view.findViewById(R.id.sports_selection)
        sportsRecyclerView.layoutManager = LinearLayoutManager(context)


        view.findViewById<ImageView>(R.id.close_selection).let {
            if (parent.viewModel.selectedSport.value == null)
                it.visibility = View.INVISIBLE
            else
                it.visibility = View.VISIBLE
            it.setOnClickListener {
                val fragmentTransaction = parent.parentFragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                fragmentTransaction.replace(R.id.find_court, SearchFragment(parent))
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }

            parent.viewModel.getAllSports().observe(viewLifecycleOwner) {
                sportsRecyclerView.adapter = SportsSelectionAdapter(it) { sport ->
                    parent.viewModel.setSport(sport)
                    val fragmentTransaction = parent.parentFragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    fragmentTransaction.replace(R.id.find_court, SearchFragment(parent))
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
        }
    }
}
