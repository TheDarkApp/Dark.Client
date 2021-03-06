package lab.maxb.dark.presentation.view

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import lab.maxb.dark.R
import lab.maxb.dark.databinding.RecognitionTaskListFragmentBinding
import lab.maxb.dark.domain.model.RecognitionTask
import lab.maxb.dark.presentation.extra.GlideApp
import lab.maxb.dark.presentation.extra.delegates.autoCleaned
import lab.maxb.dark.presentation.extra.delegates.viewBinding
import lab.maxb.dark.presentation.extra.navigate
import lab.maxb.dark.presentation.extra.observe
import lab.maxb.dark.presentation.view.adapter.RecognitionTaskListAdapter
import lab.maxb.dark.presentation.viewModel.RecognitionTaskListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class RecognitionTaskListFragment : Fragment(R.layout.recognition_task_list_fragment) {
    private val mViewModel: RecognitionTaskListViewModel by sharedViewModel()
    private val mBinding: RecognitionTaskListFragmentBinding by viewBinding()
    private var mAdapter: RecognitionTaskListAdapter by autoCleaned()
    private var mPlaceholder: Drawable by autoCleaned()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.isTaskCreationAllowed observe {
            mBinding.fab.isVisible = it
        }
        mBinding.fab.setOnClickListener {
            RecognitionTaskListFragmentDirections.navToAddTaskFragment().navigate()
        }
        mPlaceholder = AnimatedVectorDrawableCompat.create(
            requireContext(),
            R.drawable.loading_vector
        )!!
        with (mPlaceholder as Animatable) {
            stop()
            start()
        }
        mAdapter = RecognitionTaskListAdapter(GlideApp.with(this),
            !mViewModel.isTaskCreationAllowed.value
        ) {
            load(mViewModel.getImage(it))
                .transition(withCrossFade())
                .placeholder(mPlaceholder)
                .error(R.drawable.ic_error)
        }
        mBinding.recognitionTaskListRecycler.adapter = mAdapter
        mBinding.recognitionTaskListRecycler.addOnScrollListener(mAdapter.preloader.raw)
        mAdapter.onElementClickListener = { _: View, task: RecognitionTask ->
            RecognitionTaskListFragmentDirections.navToSolveTaskFragment(
                task.id
            ).navigate()
        }

        mViewModel.recognitionTaskList observe {
            mAdapter.submitData(it)
        }
    }
}