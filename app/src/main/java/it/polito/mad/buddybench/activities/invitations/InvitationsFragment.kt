package it.polito.mad.buddybench.activities.invitations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity

@AndroidEntryPoint
class InvitationsFragment(context: HomeActivity) : Fragment(R.layout.my_invitations) {

    private val viewModel = context.invitationsViewModel
    private lateinit var recyclerViewInvitations: RecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewInvitations = view.findViewById(R.id.invtationsRecyclerView)
        recyclerViewInvitations.layoutManager = LinearLayoutManager(context)

        recyclerViewInvitations.adapter = InvitationAdapter(viewModel.invitations.value?: listOf())

    }

}
