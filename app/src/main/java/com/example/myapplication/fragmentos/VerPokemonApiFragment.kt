package com.example.myapplication.fragmentos

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentVerPokemonApiBinding
import com.example.myapplication.databinding.FragmentVerPokemonBinding
import com.example.myapplication.modelo.DatosPokemon
import com.squareup.picasso.Picasso


class VerPokemonApiFragment : Fragment() {

    private var _binding: FragmentVerPokemonApiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerPokemonApiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        Log.d("Nuevo fragmento", "Hola desde fragment api pokemon ver")
        Log.d("arguments", arguments.toString())
        val bundle = arguments
        Log.d("Contenido de bundle", bundle.toString())
        if (bundle != null) {
            // Obtener el objeto DatosPokemon del Bundle
            val pokemon: DatosPokemon? = bundle.parcelable("pokemon")
            // Hacer algo con el objeto DatosPokemon
            if (pokemon != null) {
                val nombre = pokemon.nombre
                Picasso.get().load(pokemon?.imageUrl).into(binding.ivFotoPokemon)
                binding.tv.text = nombre
            }
        }
    }

    //La extension de funcion parcelable utilizara el metodo adecuado segun la version de la API
    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }
}