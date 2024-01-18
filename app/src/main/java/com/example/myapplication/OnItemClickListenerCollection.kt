package com.example.myapplication

import com.example.myapplication.modelo.PokemonColeccion
import com.example.myapplication.modelo.PokemonRecyclerView

interface OnItemClickListenerCollection {

    fun onItemClick(pokemon: PokemonColeccion)

}