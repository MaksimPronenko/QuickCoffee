package home.samples.quickcoffee.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import home.samples.quickcoffee.App
import home.samples.quickcoffee.data.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideApp(@ApplicationContext context: Context): App {
        return context as App
    }

    @Provides
    @Singleton
    fun provideRepository(): Repository {
        return Repository()
    }
}