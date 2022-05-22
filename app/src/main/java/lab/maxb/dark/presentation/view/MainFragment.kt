package lab.maxb.dark.presentation.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import lab.maxb.dark.NavGraphDirections
import lab.maxb.dark.R
import lab.maxb.dark.databinding.MainFragmentBinding
import lab.maxb.dark.presentation.extra.delegates.viewBinding
import lab.maxb.dark.presentation.extra.launch
import lab.maxb.dark.presentation.extra.navigate
import lab.maxb.dark.presentation.extra.observe
import lab.maxb.dark.presentation.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MainFragment : Fragment(R.layout.main_fragment) {
    private val mViewModel: AuthViewModel by sharedViewModel()
    private val mBinding: MainFragmentBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.profile observe {
            it.ifLoaded { profile ->
                if (profile == null)
                    openLoginView()
                else
                    mBinding.welcomeLabel.text = getString(R.string.welcome_welcome,
                    profile.user?.name ?: "Anonymous")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.signout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_sign_out -> {
            signOut()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun signOut() = launch {
        mViewModel.signOut()
        openLoginView()
    }

    private fun openLoginView()
        = NavGraphDirections.navToAuthFragment().navigate()
}