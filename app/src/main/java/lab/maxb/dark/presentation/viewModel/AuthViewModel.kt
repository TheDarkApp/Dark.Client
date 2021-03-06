package lab.maxb.dark.presentation.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import lab.maxb.dark.domain.model.AuthCredentials
import lab.maxb.dark.domain.model.Profile
import lab.maxb.dark.presentation.extra.UserSettings
import lab.maxb.dark.presentation.extra.launch
import lab.maxb.dark.presentation.repository.interfaces.ProfileRepository
import lab.maxb.dark.presentation.repository.interfaces.UsersRepository
import lab.maxb.dark.presentation.repository.room.LocalDatabase
import lab.maxb.dark.presentation.viewModel.utils.UiState
import lab.maxb.dark.presentation.viewModel.utils.stateIn
import lab.maxb.dark.presentation.viewModel.utils.valueOrNull
import org.koin.android.annotation.KoinViewModel


@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class AuthViewModel(
    private val profileRepository: ProfileRepository,
    private val usersRepository: UsersRepository,
    private val db: LocalDatabase,
    private val userSettings: UserSettings,
//    private val mGoogleSignInLogic: GoogleSignInLogic,
) : ViewModel() {
    private var _wasAuthorized = false
    private val _profile = MutableStateFlow(UiState.Loading as UiState<Profile?>)
    val profile = _profile.stateIn(UiState.Loading)

    val user = profile.filter {
        it.valueOrNull != null
    }.flatMapLatest {
        usersRepository.getUser(it.valueOrNull!!.user!!.id)
    }.stateIn(null)

    val login = MutableStateFlow("")
    val password = MutableStateFlow("")
    val showPassword = MutableStateFlow(false)
    val passwordRepeat = MutableStateFlow("")
    val isAccountNew = MutableStateFlow(false)
    val isLoading = MutableStateFlow(false)

    init {
        launch {
            profileRepository.profile.mapLatest {
                UiState.Success(it)
            }.catch {
                UiState.Error<Profile?>(it)
            }.also {
                _profile.emitAll(it)
            }
        }
    }

    fun handleNotAuthorizedYet() {
        _profile.value = UiState.Loading
    }

    fun handleAuthorizedStateChanges() = _profile.value.ifLoaded {
        if (it != null)
            _wasAuthorized = true
        else if (_wasAuthorized) {
            _wasAuthorized = false
            signOut()
        }
    }

    fun authorize() {
        _profile.value = UiState.Loading
        profileRepository.sendCredentials(AuthCredentials(
            login.value,
            password.value,
            isAccountNew.value,
        ))
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    fun authorizeByOAUTHProvider(login: String, name: String, authCode: String) {
        TODO()
    }

    fun hasEmptyFields() =
        login.value.isEmpty() ||
        password.value.isEmpty() ||
        isAccountNew.value && passwordRepeat.value.isEmpty()

    fun isPasswordsNotMatch() = isAccountNew.value && password.value != passwordRepeat.value

    fun signOut() = launch(Dispatchers.Default) {
//        mGoogleSignInLogic.signOut()
        userSettings.token = ""
        userSettings.login = ""
        isLoading.value = false
        db.clearAllTables()
    }
}

context(ViewModel)
inline val ProfileRepository.profileState get() = profile.stateIn(null)