package com.example.myapplication.adaptadoresrv.pokemoncoleccion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.OnItemClickListenerCollection
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemPokemonApiBinding
import com.example.myapplication.modelo.PokemonColeccion
import com.squareup.picasso.Picasso

class AdaptadorPokemonColeccion(
    private var listaPokemon: MutableList<PokemonColeccion>,
    private val itemClickListener: OnItemClickListenerCollection
) : RecyclerView.Adapter<AdaptadorPokemonColeccion.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        // Aquí puedes inicializar las vistas de tu elemento de la lista si es necesario

        val binding = ItemPokemonApiBinding.bind(view)


        init {
            view.setOnClickListener(this)
        }

        fun bind(pokemon: PokemonColeccion) {
            binding.tvNombrePokemon.text = pokemon.nombre?.capitalize()
            Picasso.get().load(pokemon.imagen).into(binding.ivFotoPokemon)
        }

        override fun onClick(p0: View?) {
            val posicion: Int = adapterPosition
            val pokemon: PokemonColeccion = listaPokemon[posicion]
            itemClickListener.onItemClick(pokemon)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon_coleccion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Aquí puedes actualizar las vistas con la información del Pokémon en la posición dada
        val pokemonActual = listaPokemon[position]
        holder.bind(pokemonActual)
    }

    override fun getItemCount(): Int = listaPokemon.size
}