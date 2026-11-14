package com.openfang.mobile.di

import com.openfang.mobile.data.SetupStateStore
import com.openfang.mobile.service.ProotOrchestrator
import com.openfang.mobile.service.ProotService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt modules for the app component.
 *
 * - [AppModule] provides app-scope singletons: orchestrator, setup-state store.
 * - [ServiceModule] is installed into the [ServiceComponent] so [ProotService]
 *   can receive [ProotOrchestrator].
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOrchestrator(@ApplicationContext ctx: android.content.Context): ProotOrchestrator =
        ProotOrchestrator(ctx)

    @Provides
    @Singleton
    fun provideSetupStateStore(@ApplicationContext ctx: android.content.Context): SetupStateStore =
        SetupStateStore(ctx)
}

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    // ProotService gets ProotOrchestrator via @Inject (the orchestrator
    // is a Singleton, so it's the same instance the activity sees).
}
