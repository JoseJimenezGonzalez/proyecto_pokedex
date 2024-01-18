package com.example.myapplication.fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.OnItemClickListenerCollection
import com.example.myapplication.R
import com.example.myapplication.adaptadoresrv.pokemoncoleccion.AdaptadorPokemonColeccion
import com.example.myapplication.databinding.FragmentVerPokemonBinding
import com.example.myapplication.modelo.PokemonColeccion
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Job

class VerPokemonFragment : Fragment(), OnItemClickListenerCollection{

    private var _binding: FragmentVerPokemonBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<PokemonColeccion>
    private lateinit var adaptador: AdaptadorPokemonColeccion
    private lateinit var dbRef: DatabaseReference

    private lateinit var stRef: StorageReference

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerPokemonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo

        dbRef = FirebaseDatabase.getInstance().reference

        stRef = FirebaseStorage.getInstance().reference

        job = Job()

        configurarRecyclerView()
        configurarSearchView()

    }

    private fun configurarSearchView() {
        // Configurar el SearchView
        binding.sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun showPopupMenu(view: View?) {
        // Crear instancia de PopupMenu
        val popupMenu = view?.let { PopupMenu(context, it) }

        // Inflar el menú desde el archivo XML
        popupMenu?.menuInflater?.inflate(R.menu.popup_menu, popupMenu.menu)

        // Establecer un listener para manejar clics en las opciones del menú
        popupMenu?.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_sort_aph -> {
                    // Lógica para la opción "ordenar alfabeticamente"
                    lista.sortBy { pokemon ->
                        pokemon.nombre
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_rating -> {
                    // Lógica para la opción "ordenar por nivel"
                    lista.sortByDescending { pokemon ->
                        pokemon.nivel
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                else -> false
            }
        }
        // Mostrar el menú emergente
        popupMenu?.show()
    }

    private fun configurarRecyclerView() {
        lista = mutableListOf()

        // Obtén la referencia a la base de datos
        val dbRef = FirebaseDatabase.getInstance().reference

        // Configura el adaptador con el fragmento actual (this) como contexto
        adaptador = AdaptadorPokemonColeccion(lista, this)
        recycler = requireView().findViewById(R.id.rvListaPokemonColeccion)  // Usar requireView() en lugar de findViewById

        // Configura el RecyclerView
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.setHasFixedSize(true)

        // Agrega el ValueEventListener para actualizar la lista cuando cambian los datos en la base de datos
        dbRef.child("Pokemon").child("Coleccion").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoPokemon = hijo?.getValue(PokemonColeccion::class.java)
                    lista.add(pojoPokemon!!)
                }
                adaptador.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
    }

    override fun onItemClick(pokemon: PokemonColeccion) {

    }

}