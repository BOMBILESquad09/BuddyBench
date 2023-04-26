package it.polito.mad.buddybench.activities.court

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.ui.AppBarConfiguration
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.databinding.ActivityCourtBinding
import it.polito.mad.buddybench.viewmodels.ReservationViewModel

@AndroidEntryPoint
class CourtActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCourtBinding
    val reservationViewModel by viewModels<ReservationViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCourtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = CourtFragment()

        // ** Check intent for edit mode
        val editMode = intent.getBooleanExtra("edit", false);
        if (editMode) {
            val args = Bundle()
            args.putBoolean("edit", true)

            val courtName = intent.getStringExtra("courtName")
            args.putString("courtName", courtName)

            val sport = intent.getStringExtra("sport")
            args.putString("sport", sport)

            val date = intent.getStringExtra("date")
            args.putString("date", date)

            val email = intent.getStringExtra("email")
            args.putString("email", email)

            val startTime = intent.getIntExtra("startTime", -1)
            args.putInt("startTime", startTime)

            val endTime = intent.getIntExtra("endTime",-1)
            args.putInt("endTime", endTime)
            fragment.arguments = args
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerCourt, fragment)
            .commit()

    }

}