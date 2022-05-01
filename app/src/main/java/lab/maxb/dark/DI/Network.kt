package lab.maxb.dark.DI

import lab.maxb.dark.Presentation.Extra.UserSettings
import lab.maxb.dark.Presentation.Repository.Network.Dark.AuthInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val MODULE_network = module {
    single { UserSettings(androidContext()) }
    single { AuthInterceptor(get()) }
}