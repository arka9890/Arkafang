package com.openfang.mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class. Annotated with @HiltAndroidApp so Hilt can inject
 * into the activity / services / view-models declared later.
 */
@HiltAndroidApp
class OpenFangApp : Application()
