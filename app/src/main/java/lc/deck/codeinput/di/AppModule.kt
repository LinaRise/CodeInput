package lc.deck.codeinput.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lc.deck.codeinput.system.schedulers.AppSchedulers
import lc.deck.codeinput.system.schedulers.SchedulersProvider
import java.util.Date
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object{
        @Provides
        @Singleton
        fun provideGson(): Gson = with(GsonBuilder()) {
            serializeNulls()
            create()
        }

        @Provides
        @Singleton
        fun provideSchedulers(): SchedulersProvider = AppSchedulers()
    }
}