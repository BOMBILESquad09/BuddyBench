package it.polito.mad.buddybench.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R

@AndroidEntryPoint
class InvitesFragment(context: HomeActivity) : Fragment(R.layout.my_invitations) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
