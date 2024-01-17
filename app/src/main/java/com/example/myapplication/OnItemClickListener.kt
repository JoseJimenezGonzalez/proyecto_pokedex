package com.example.myapplication

import com.example.myapplication.modelo.PokemonApi
import com.example.myapplication.modelo.PokemonRecyclerView

interface OnItemClickListener {
    fun onItemClick(pokemon: PokemonRecyclerView)
}