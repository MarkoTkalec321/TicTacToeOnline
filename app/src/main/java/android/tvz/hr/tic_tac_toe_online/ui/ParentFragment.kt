package android.tvz.hr.tic_tac_toe_online.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.tvz.hr.tic_tac_toe_online.R
import android.tvz.hr.tic_tac_toe_online.data.UserDataStore
import android.tvz.hr.tic_tac_toe_online.databinding.FragmentParentBinding
import android.tvz.hr.tic_tac_toe_online.ui.dialogs.DialogUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ParentFragment : Fragment() {

    private var _binding: FragmentParentBinding? = null
    private val binding get() = _binding!!
    private lateinit var userDataStore: UserDataStore

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParentBinding.inflate(inflater, container, false)
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        adapter.addFragment(LobbyFragment(), "Lobby")
        adapter.addFragment(LeaderboardsFragment(), "Leaderboards")

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.fragmentToolbar
        val logoutButton = toolbar.logoutButton
        logoutButton.setOnClickListener{
            DialogUtils.showLogoutConfirmationDialog(requireContext()) {
                DialogUtils.logoutUser(requireContext(), viewLifecycleOwner, findNavController(), binding, R.id.action_ParentFragment_to_LoginFragment)
            }
        }

        userDataStore = UserDataStore(requireContext())

        lifecycleScope.launch {
            userDataStore.userPreferencesFlow.collect { userPreferences ->
                binding.fragmentToolbar.playerNameTextview.text = "Player: ${userPreferences.username}"
            }
        }
    }

}
