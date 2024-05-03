package android.tvz.hr.tic_tac_toe_online

import android.os.Bundle
import android.service.notification.NotificationListenerService.Ranking
import android.tvz.hr.tic_tac_toe_online.databinding.ActivityMainBinding
import android.tvz.hr.tic_tac_toe_online.ui.LobbyFragment
//import android.tvz.hr.tic_tac_toe_online.ui.SectionsPagerAdapter
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}