package com.example.fido_newsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class NewsSource(
    val id: String,
    val name: String,
    val description: String? = null
) : Parcelable