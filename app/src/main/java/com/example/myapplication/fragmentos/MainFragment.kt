package com.example.myapplication.fragmentos

import android.os.Bundle
import android.view.ContentInfo
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.OnItemClickListener
import com.example.myapplication.adaptadoresrv.pokemonapi.AdaptadorPokemonApi
import com.example.myapplication.api.APIService
import com.example.myapplication.api.APIServiceList
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.modelo.DatosPokemonListaApi
import com.example.myapplication.modelo.PokemonApi
import com.example.myapplication.modelo.PokemonRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class MainFragment() : Fragment(), CoroutineScope, OnItemClickListener{

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var adaptador: AdaptadorPokemonApi

    private lateinit var servicio: APIServiceList

    private lateinit var servicioApiService: APIService

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
                val listaDeRespuestaPokemon = respuesta.body()?.results
                val listaUrlPokemon = obtenerListaDeUrlDePokemon(listaDeRespuestaPokemon)
                val listaParaMostrarRecyclerView = obtenerObjetoPokemonAPartirDeUrl(listaUrlPokemon)
            }else{

            }
        }
    }

    private fun obtenerListaDeUrlDePokemon(listaDePokemon: MutableList<DatosPokemonListaApi>?): MutableList<String>{
        val listaUrl = mutableListOf<String>()
        listaDePokemon?.forEach { pokemon ->
            val urlPokemon = pokemon.url
            listaUrl.add(urlPokemon)
        }
        return listaUrl
    }
    private suspend fun obtenerObjetoPokemonAPartirDeUrl(listaDeUrl: MutableList<String>): MutableList<PokemonRecyclerView>{
        var listaPokemonRecyclerView = mutableListOf<PokemonRecyclerView>()
        listaDeUrl.forEach { url ->
            val respuesta = servicioApiService.obtenerDatosPorUrlPokemon(url)
            if(respuesta.isSuccessful){
                val pokemonApi = respuesta.body()
                val pokemonRecyclerView = pokemonApi?.sprites?.other?.officialArtWork?.let { PokemonRecyclerView(pokemonApi.name, it.front_default) }
                if (pokemonRecyclerView != null) {
                    listaPokemon.add(pokemonRecyclerView)
                }
            }
        }
        return listaPokemonRecyclerView
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.rvListaPokemonAPI
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adaptador = AdaptadorPokemonApi(listaPokemon, this)
        recyclerView.adapter = adaptador
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onItemClick(pokemon: PokemonRecyclerView) {

    }
}