package home.samples.quickcoffee.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import home.samples.quickcoffee.data.Repository
import home.samples.quickcoffee.ui.cafe.CafeViewModel
import home.samples.quickcoffee.ui.cafe.CafeViewModelFactory
import home.samples.quickcoffee.ui.login.LoginViewModel
import home.samples.quickcoffee.ui.login.LoginViewModelFactory
import home.samples.quickcoffee.ui.registration.RegistrationViewModel
import home.samples.quickcoffee.ui.registration.RegistrationViewModelFactory

@Module
@InstallIn(SingletonComponent::class)
class PresentationModule {
    @Provides
    fun provideRegistrationViewModel(
        repository: Repository
    ): RegistrationViewModel {
        return RegistrationViewModel(
            repository
        )
    }

    @Provides
    fun provideRegistrationViewModelFactory(registrationViewModel: RegistrationViewModel): RegistrationViewModelFactory {
        return RegistrationViewModelFactory(registrationViewModel)
    }

    @Provides
    fun provideLoginViewModel(
        repository: Repository
    ): LoginViewModel {
        return LoginViewModel(
            repository
        )
    }

    @Provides
    fun provideLoginViewModelFactory(loginViewModel: LoginViewModel): LoginViewModelFactory {
        return LoginViewModelFactory(loginViewModel)
    }

    @Provides
    fun provideCafeViewModel(
        repository: Repository,
        application: Application
    ): CafeViewModel {
        return CafeViewModel(
            repository,
            application
        )
    }

    @Provides
    fun provideCafeViewModelFactory(cafeViewModel: CafeViewModel): CafeViewModelFactory {
        return CafeViewModelFactory(cafeViewModel)
    }
}