package home.samples.quickcoffee.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import home.samples.quickcoffee.App
import home.samples.quickcoffee.data.Repository
import home.samples.quickcoffee.ui.cafe.CafeViewModel
import home.samples.quickcoffee.ui.cafe.CafeViewModelFactory
import home.samples.quickcoffee.ui.login.LoginViewModel
import home.samples.quickcoffee.ui.login.LoginViewModelFactory
import home.samples.quickcoffee.ui.map.MapViewModel
import home.samples.quickcoffee.ui.map.MapViewModelFactory
import home.samples.quickcoffee.ui.menu.MenuViewModel
import home.samples.quickcoffee.ui.menu.MenuViewModelFactory
import home.samples.quickcoffee.ui.order.OrderViewModel
import home.samples.quickcoffee.ui.order.OrderViewModelFactory
import home.samples.quickcoffee.ui.registration.RegistrationViewModel
import home.samples.quickcoffee.ui.registration.RegistrationViewModelFactory
import javax.inject.Singleton

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
    @Singleton
    fun provideCafeViewModel(
        repository: Repository,
        application: App
    ): CafeViewModel {
        return CafeViewModel(
            repository,
            application
        )
    }

    @Provides
    @Singleton
    fun provideCafeViewModelFactory(cafeViewModel: CafeViewModel): CafeViewModelFactory {
        return CafeViewModelFactory(cafeViewModel)
    }

    @Provides
    @Singleton
    fun provideMenuViewModel(
        repository: Repository,
        application: App
    ): MenuViewModel {
        return MenuViewModel(
            repository,
            application
        )
    }

    @Provides
    @Singleton
    fun provideMenuViewModelFactory(menuViewModel: MenuViewModel): MenuViewModelFactory {
        return MenuViewModelFactory(menuViewModel)
    }

    @Provides
    @Singleton
    fun provideOrderViewModel(
        application: App
    ): OrderViewModel {
        return OrderViewModel(
            application
        )
    }

    @Provides
    @Singleton
    fun provideOrderViewModelFactory(orderViewModel: OrderViewModel): OrderViewModelFactory {
        return OrderViewModelFactory(orderViewModel)
    }

    @Provides
    fun provideMapViewModel(
        repository: Repository
    ): MapViewModel {
        return MapViewModel(
            repository
        )
    }

    @Provides
    fun provideMapViewModelFactory(mapViewModel: MapViewModel): MapViewModelFactory {
        return MapViewModelFactory(mapViewModel)
    }
}