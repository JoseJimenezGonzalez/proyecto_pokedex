package com.example.myapplication.api

import com.example.myapplication.modelo.PokemonListaApi
import retrofit2.Call
import retrofit2.http.GET

interface APIServiceList {
    @GET("pokemon")
    fun getPokemonList(): Call<PokemonListaApi>
}