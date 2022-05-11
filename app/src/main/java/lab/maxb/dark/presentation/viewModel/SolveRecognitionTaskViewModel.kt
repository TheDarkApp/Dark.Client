package lab.maxb.dark.presentation.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import lab.maxb.dark.domain.model.Role
import lab.maxb.dark.domain.operations.solve
import lab.maxb.dark.presentation.repository.interfaces.ProfileRepository
import lab.maxb.dark.presentation.repository.interfaces.RecognitionTasksRepository
import lab.maxb.dark.presentation.viewModel.utils.firstNotNull
import lab.maxb.dark.presentation.viewModel.utils.stateIn
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SolveRecognitionTaskViewModel(
    private val recognitionTasksRepository: RecognitionTasksRepository,
    profileRepository: ProfileRepository,
) : ViewModel() {
    private val _id = MutableStateFlow<String?>(null)
    private val profile = profileRepository.profileState
    val answer = MutableStateFlow("")

    fun init(id: String) {
        if (_id.value == id) return
        _id.value = id
        answer.value = ""
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val recognitionTask = _id.filterNotNull().flatMapLatest {
        recognitionTasksRepository.getRecognitionTask(it)
    }.stateIn(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val isReviewMode get() = profile.mapLatest {
        when (it?.role) {
            Role.MODERATOR, Role.ADMINISTRATOR -> true
            else -> false
        }
    }.stateIn(false)

    suspend fun mark(isAllowed: Boolean) {
        recognitionTask.firstNotNull().apply {
            reviewed = isAllowed
        }.also {
            recognitionTasksRepository.markRecognitionTask(it)
        }
    }

    suspend fun solveRecognitionTask() = recognitionTask.firstNotNull().let {
        it.solve(answer.firstNotNull()).also { isSolution ->
            if (isSolution)
                recognitionTasksRepository.deleteRecognitionTask(it)
        }
    }
}