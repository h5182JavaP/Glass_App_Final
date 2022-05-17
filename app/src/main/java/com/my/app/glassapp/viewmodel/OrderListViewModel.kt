package com.my.app.glassapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.my.app.glassapp.database.DBRoom
import com.my.app.glassapp.extensions.zipLiveData

public class OrderListViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext by lazy { getApplication<Application>() }
    private val dbRoom by lazy { DBRoom.getInstance(appContext).dao() }

    val sguLiveData by lazy { dbRoom.sguLiveData }
    private val dguLiveData by lazy { dbRoom.dguLiveData }
    private val laminationLiveData by lazy { dbRoom.laminationLiveData }
    private val annealedLiveData by lazy { dbRoom.annealedLiveData }
    private val ldguLiveData by lazy { dbRoom.ldguLiveData }

    val mediatorLiveData = zipLiveData(sguLiveData, dguLiveData, laminationLiveData, annealedLiveData, ldguLiveData)

}