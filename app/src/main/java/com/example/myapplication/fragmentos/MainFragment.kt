package com.example.myapplication.fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.adaptadoresrv.pokemonapi.AdaptadorPokemonApi
import com.example.myapplication.api.APIServiceList
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.modelo.PokemonRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class MainFragment() : Fragment(), CoroutineScope {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var adaptador: AdaptadorPokemonApi

    private lateinit var servicio: APIServiceList

    private var listaPokemon = mutableListOf<PokemonRecyclerView>()

    private var job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo

    }

    private fun obtenerRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/pokemon/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun cargarListaDePokemon() {
        val llamada = servicio.getPokemonList()
        launch {
            val respuesta = llamada.execute()
            if(respuesta.isSuccessful){
                val listaDePokemon = respuesta.body()?.results
            }else{

            }
        }
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}