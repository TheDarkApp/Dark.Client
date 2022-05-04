package lab.maxb.dark.Presentation.View

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import lab.maxb.dark.NavGraphDirections
import lab.maxb.dark.Presentation.Extra.Delegates.viewBinding
import lab.maxb.dark.Presentation.Extra.observe
import lab.maxb.dark.Presentation.ViewModel.UserViewModel
import lab.maxb.dark.R
import lab.maxb.dark.databinding.AuthHandleFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AuthHandleFragment : Fragment(R.layout.auth_handle_fragment) {
    private val mViewModel: UserViewModel by sharedViewModel()
    private val mBinding: AuthHandleFragmentBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.profile observe {
            it.ifLoaded { profile ->
                findNavController().navigate(
                    if (profile == null)
                        NavGraphDirections.actionGlobalLoginFragment()
                    else
                        AuthHandleFragmentDirections.actionAuthHandleFragmentToMainFragment()
                )
            }
        }
    }
}