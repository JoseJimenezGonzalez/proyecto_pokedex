package com.example.myapplication.modelo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PokemonColeccion
    (
            var nombre: String? = null,
            var naturaleza: String? = null,
            var id: String? = null,
            var nivel: Int? = null,
            var imagen: String? = null
            ):Parcelable
