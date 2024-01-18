package com.example.myapplication.fragmentos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.OnItemClickListener
import com.example.myapplication.R
import com.example.myapplication.adaptadoresrv.pokemonapi.AdaptadorPokemonApi
import com.example.myapplication.api.APIService
import com.example.myapplication.api.APIServiceList
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.modelo.DatosPokemon
import com.example.myapplication.modelo.DatosPokemonListaApi
import com.example.myapplication.modelo.PokemonRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
        obtenerRetrofit()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        setupRecyclerView()
        cargarListaDePokemon()
    }

    private fun obtenerRetrofit(): Retrofit {
        servicio = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIServiceList::class.java)

        servicioApiService = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)

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
                obtenerObjetoPokemonAPartirDeUrl(listaUrlPokemon)
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
    private suspend fun obtenerObjetoPokemonAPartirDeUrl(listaDeUrl: MutableList<String>) {
        listaDeUrl.forEach { url ->
            val respuesta = servicioApiService.obtenerDatosPorUrlPokemon(url)
            if (respuesta.isSuccessful) {
                val pokemonApi = respuesta.body()
                val nombrePokemon = pokemonApi?.name
                val urlFotoPokemon = pokemonApi?.sprites?.other?.officialArtWork?.let{it.front_default}
                val pokemonRecyclerView = PokemonRecyclerView(nombrePokemon!!, urlFotoPokemon!!)
                withContext(Dispatchers.Main) {
                    // Cambiar al hilo principal antes de actualizar la interfaz de usuario
                    listaPokemon.add(pokemonRecyclerView)
                    adaptador.notifyDataSetChanged()
                }
            }
        }
    }

    private suspend fun obtenerPokemonAPartirDeNombre(nombrePokemon: String): DatosPokemon {
        return try {
            val respuesta = servicioApiService.obtenerDatosPorUrlPokemon("pokemon/$nombrePokemon")
            if (respuesta.isSuccessful) {
                val pokemonApi = respuesta.body()
                if (pokemonApi != null) {
                    val urlFoto = pokemonApi.sprites.other.officialArtWork.front_default
                    val nombre = pokemonApi.name
                    val peso = pokemonApi.weight.toDouble()
                    val altura = pokemonApi.height.toDouble()
                    var tipos = ""
                    pokemonApi.types.forEach { tipo ->
                        tipos += "$tipo\n"
                    }
                    val estadisticas = "No tengo ganas"
                    DatosPokemon(
                        urlFoto,
                        nombre,
                        peso,
                        altura,
                        tipos,
                        estadisticas
                    )
                } else {
                    DatosPokemon("", "", 0.0, 0.0, "", "")
                }
            } else {
                DatosPokemon("", "", 0.0, 0.0, "", "")
            }
        } catch (e: Exception) {
            // Manejar excepciones, puedes lanzar la excepción o devolver un valor predeterminado
            DatosPokemon("", "", 0.0, 0.0, "", "")
        }
    }




    private fun setupRecyclerView() {
        val recyclerView = binding.rvListaPokemonAPI
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adaptador = AdaptadorPokemonApi(listaPokemon, this)
        recyclerView.adapter = adaptador
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onItemClick(pokemon: PokemonRecyclerView) {
        Log.d("Nombre del pokemon", pokemon.nombre)
        lifecycleScope.launch {
            val objetoPokemon = obtenerPokemonAPartirDeNombre(pokemon.nombre)
            Log.d("Nombre pokemon objeto", objetoPokemon.nombre)
            //Pasar el objeto pokemon al otro fragment
            if(objetoPokemon.nombre.isEmpty()){
                Toast.makeText(context, "No existe ese pokemon", Toast.LENGTH_SHORT).show()
            }else{
                val bundle = Bundle()
                bundle.putParcelable("pokemon", objetoPokemon)
                val fragment = VerPokemonApiFragment()
                fragment.arguments = bundle
                Log.d("Cont bundle", bundle.toString())
                findNavController().navigate(R.id.action_mainFragment_to_verPokemonApiFragment, bundle)
            }

        }
    }
}