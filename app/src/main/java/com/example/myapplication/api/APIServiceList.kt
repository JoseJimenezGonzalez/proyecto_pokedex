package com.example.myapplication.api

import com.example.myapplication.modelo.PokemonListaApi
import retrofit2.Call
import retrofit2.http.GET

interface APIServiceList {
    @GET("pokemon")
    fun getPokemonList(): Call<PokemonListaApi>
}

//¿Por qué se pone @GET("pokemon")?
//Por ejemplo, si la base de la URL de la API es "https://api.example.com/", la solicitud completa
// sería "https://api.example.com/pokemon" cuando se llama a getPokemonList().