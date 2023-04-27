package it.polito.mad.buddybench.activities.court

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.databinding.FragmentCourtReservationBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CourtReservationFragment : Fragment() {

    private var _binding: FragmentCourtReservationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCourtReservationBinding.inflate(inflater, container, false)
        return binding.root

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}