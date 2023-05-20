package it.polito.mad.buddybench.activities.invitations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.viewmodels.InvitationsViewModel

@AndroidEntryPoint
class InvitationsFragment(context: HomeActivity) : Fragment(R.layout.my_invitations) {

    private val viewModel by activityViewModels<InvitationsViewModel> ()
    private lateinit var recyclerViewInvitations: RecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewInvitations = view.findViewById(R.id.invtationsRecyclerView)
        recyclerViewInvitations.layoutManager = LinearLayoutManager(context)

        val onAccept: (ReservationDTO) -> Unit = {
            viewModel.acceptInvitation(it)
        }

        val onDecline: (ReservationDTO) -> Unit = {
            viewModel.refuseInvitation(it)
        }

        recyclerViewInvitations.adapter = InvitationAdapter(viewModel.invitations.value?: listOf(), onAccept, onDecline )
        viewModel.invitations.observe(viewLifecycleOwner){
            val diffsUtils = InvitationsDiffsUtils((recyclerViewInvitations.adapter as InvitationAdapter).invitations, it)
            val diffs = DiffUtil.calculateDiff(diffsUtils)
            (recyclerViewInvitations.adapter as InvitationAdapter).invitations = it
            diffs.dispatchUpdatesTo(recyclerViewInvitations.adapter!!)
        }


    }

}
