package it.polito.mad.buddybench.activities.court

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.databinding.FragmentCourtReservationBinding
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.viewmodels.CourtViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CourtReservationFragment : Fragment() {

    private var _binding: FragmentCourtReservationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // ** UI Elements
    private lateinit var switch: SwitchCompat

    // ** Court LiveData by ViewModel
    private val courtViewModel by activityViewModels<CourtViewModel>()
    private val reservationViewModel by activityViewModels<ReservationViewModel>()

    // ** Data
    private lateinit var user: UserDTO
    private lateinit var courtToReserve: CourtDTO
    private lateinit var selectedDate: LocalDate

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