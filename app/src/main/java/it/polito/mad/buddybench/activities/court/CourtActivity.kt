package it.polito.mad.buddybench.activities.court

import android.app.Activity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.databinding.ActivityCourtBinding
import it.polito.mad.buddybench.viewmodels.ReservationViewModel

@AndroidEntryPoint
class CourtActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourtBinding
    private val reservationViewModel by viewModels<ReservationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_court, CourtFragment()).commitNow()
    }
}