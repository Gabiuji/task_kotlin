package com.example.appkotlines.ui.model

import android.os.Parcelable
import com.example.appkotlines.ui.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: String = "",
    var status: Int = 0,
    var description: String = ""
):Parcelable{
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?:""
    }
}
