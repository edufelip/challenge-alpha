package com.edufelip.challengealpha.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class People(
    val id: String,
    val birthYear: String,
    val eyeColor: String,
    val films: List<String>,
    val gender: String,
    val hairColor: String,
    val height: String,
    val homeworldUrl: String,
    val mass: String,
    val name: String,
    val skinColor: String,
    val species: List<String>,
    val starships: List<String>,
    val url: String,
    val vehicles: List<String>,
    val imageUrl: String,
) : Parcelable