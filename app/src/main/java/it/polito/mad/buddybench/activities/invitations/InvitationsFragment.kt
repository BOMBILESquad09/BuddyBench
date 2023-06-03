package it.polito.mad.buddybench.activities.invitations

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.viewmodels.InvitationsViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel

@AndroidEntryPoint
class InvitationsFragment(context: HomeActivity) : Fragment(R.layout.my_invitations) {

    private val viewModel by activityViewModels<InvitationsViewModel> ()
    private val reservationViewModel by activityViewModels<ReservationViewModel> ()
    private lateinit var recyclerViewInvitations: RecyclerView
    private lateinit var emptyLL: LinearLayout
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var progress: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.getAll()
            swipeRefresh.isRefreshing = false
        }
        recyclerViewInvitations = view.findViewById(R.id.invtationsRecyclerView)
        recyclerViewInvitations.layoutManager = LinearLayoutManager(context)

        emptyLL = view.findViewById(R.id.empty)

        progress = view.findViewById(R.id.progress_layout)
        viewModel.loading.observe(viewLifecycleOwner) {

            if (it == null || it) {
                progress.visibility = View.VISIBLE

            } else {
                progress.visibility = View.GONE
            }
        }
        val onAccept: (ReservationDTO) -> Unit = {
            viewModel.acceptInvitation(it){
            }
        }

        val onDecline: (ReservationDTO) -> Unit = {
            viewModel.refuseInvitation(it){
            }
        }

        recyclerViewInvitations.adapter = InvitationAdapter(viewModel.invitations.value?: listOf(), onAccept, onDecline, true, null)
        viewModel.invitations.observe(viewLifecycleOwner){
            val diffsUtils = InvitationsDiffsUtils((recyclerViewInvitations.adapter as InvitationAdapter).invitations, it)
            val diffs = DiffUtil.calculateDiff(diffsUtils)
            (recyclerViewInvitations.adapter as InvitationAdapter).invitations = it
            diffs.dispatchUpdatesTo(recyclerViewInvitations.adapter!!)

            if(it.isEmpty()){
                val fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
                val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)

                recyclerViewInvitations.postOnAnimation {
                    recyclerViewInvitations.visibility = View.GONE
                    emptyLL.postOnAnimation {
                        emptyLL.visibility = View.VISIBLE
                    }
                    emptyLL.startAnimation(fadeIn)

                }
                fadeIn.duration = 400
                fadeOut.duration = fadeIn.duration
                recyclerViewInvitations.startAnimation(fadeOut)
                emptyLL.visibility = View.GONE
                recyclerViewInvitations.visibility = View.VISIBLE

            } else{
                emptyLL.visibility = View.GONE
                recyclerViewInvitations.visibility = View.VISIBLE
            }

        }


    }

}
