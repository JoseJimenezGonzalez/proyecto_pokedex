package com.example.myapplication.api

import com.example.myapplication.modelo.PokemonApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun obtenerDatosPorUrlPokemon(@Url url: String):Response<PokemonApi>
}

//¿Por que se usa con suspend?
//El uso de suspend en este caso indica que la función getDataByCod se puede llamar de manera
//asíncrona dentro de una corutina, lo que es útil en entornos donde se utilizan corutinas de
//Kotlin para gestionar operaciones asíncronas de manera eficiente.