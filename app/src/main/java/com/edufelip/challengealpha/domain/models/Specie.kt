package com.edufelip.challengealpha.domain.models

data class Specie(
    val averageHeight: String,
    val averageLifespan: String,
    val classification: String,
    val designation: String,
    val eyeColors: String,
    val homeworld: String,
    val language: String,
    val name: String,
    val skinColors: String,
    val url: String,
    val people: List<String>,
    val films: List<String>,
)