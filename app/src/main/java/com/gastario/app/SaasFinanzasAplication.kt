package com.gastario.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


//en adroidManifiest poner la app ->  android:name=".SaasFinanzasAplication"
@HiltAndroidApp
class SaasFinanzasAplication : Application()