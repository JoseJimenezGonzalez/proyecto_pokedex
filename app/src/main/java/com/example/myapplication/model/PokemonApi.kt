package com.example.myapplication.model

data class PokemonApi
    (
    val height: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val stats: List<Stat>
            )
