package it.polito.mad.buddybench.activities.court

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.databinding.ActivityCourtBinding

@AndroidEntryPoint
class CourtActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCourtBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCourtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = CourtFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerCourt, fragment)
            .commit()

    }

}