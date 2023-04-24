package it.polito.mad.buddybench.activities.findcourt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.compose.ui.text.capitalize
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
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
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.CourtViewModel

@AndroidEntryPoint
class SearchFragment(val parent: FindCourtFragment): Fragment(R.layout.activity_search_court) {

    private val courtViewModel by viewModels<CourtViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        val b = view.findViewById<ImageButton>(R.id.change_sport_button)
        val textNearButton = view.findViewById<TextView>(R.id.textView12)
        val textUser = view.findViewById<TextView>(R.id.textView11)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)

        textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)

        b.setOnClickListener{
            parent.fragmentManager.switchFragment(States.SPORTS_SELECTION)
            textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)
        }

        parent.viewModel.selectedSport.observe(viewLifecycleOwner){
            val iconDrawable = ContextCompat.getDrawable(view.context,
                Sports.sportToIconDrawable(
                    it
                )
            )
            val wrappedDrawable = DrawableCompat.wrap(iconDrawable!!)
            wrappedDrawable.mutate().setTint(Color.WHITE)
            val bitmap = wrappedDrawable.toBitmap(160, 160)
            b.setImageBitmap(bitmap)

            textNearButton.text = getString(R.string.court_search_phrase, it.toString().lowercase().replaceFirstChar { c -> c.uppercase() })

            val l = courtViewModel.getCourtsBySport(it)
            recyclerView?.adapter = CourtSearchAdapterAdapter(l)
            recyclerView?.layoutManager = LinearLayoutManager(view.context)
            textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)

            searchEditText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val filteredData = l.filter {
                        it.location.contains(s.toString(), ignoreCase = true) || it.name.contains(s.toString(), ignoreCase = true)
                    }
                    recyclerView.adapter = CourtSearchAdapterAdapter(filteredData)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        super.onViewCreated(view, savedInstanceState)
    }





}