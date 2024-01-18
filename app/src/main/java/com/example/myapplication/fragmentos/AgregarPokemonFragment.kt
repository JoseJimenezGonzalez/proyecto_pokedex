package com.example.myapplication.fragmentos

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.myapplication.databinding.FragmentAgregarPokemonBinding
import com.example.myapplication.modelo.PokemonColeccion
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AgregarPokemonFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentAgregarPokemonBinding? = null
    private val binding get() = _binding!!

    private var urlImagen: Uri? = null

    private lateinit var imagenPokemon: ImageView

    private lateinit var dbRef: DatabaseReference

    private lateinit var stRef: StorageReference

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgregarPokemonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo

        job = Job()

        imagenPokemon = binding.ivImagenPokemon

        dbRef = FirebaseDatabase.getInstance().reference

        stRef = FirebaseStorage.getInstance().reference

        configurarBotonImageViewAccesoGaleria()
        
        binding.btnAgregarPokemon.setOnClickListener {
            val nombrePokemon = binding.tietNombrePokemon.text.toString().trim().capitalize()
            val naturalezaPokemon = binding.tietNaturalezaPokemon.text.toString().trim().capitalize()
            val nivelPokemon = binding.tietNivelPokemon.text.toString().toInt()
            if(nombrePokemon.isEmpty() || naturalezaPokemon.isEmpty() || nivelPokemon.toString().isEmpty()){
                Toast.makeText(context, "Falla algun campo", Toast.LENGTH_SHORT).show()
            }else if(urlImagen == null){
                Toast.makeText(context, "Falta seleccionar imagen", Toast.LENGTH_SHORT).show()
            }else{
                val idGenerado: String? = dbRef.child("Pokemon").child("Coleccion").push().key
                Log.d("id generado", idGenerado.toString())
                launch {
                    val urlFotoFirebase = guardarImagenCover(stRef, idGenerado!!, urlImagen!!)
                    escribirPokemon(dbRef, idGenerado, nombrePokemon, naturalezaPokemon, urlFotoFirebase, nivelPokemon)
                    //Fallo
                    val esValido = escribirPokemon(dbRef, idGenerado, nombrePokemon, naturalezaPokemon, urlFotoFirebase, nivelPokemon).isSuccessful
                    Log.d("¿Bien?", esValido.toString())
                    withContext(Dispatchers.Main) {
                        // Mostrar el Toast en el hilo principal
                        Toast.makeText(context, "Creado con exito", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            urlImagen = uri
            imagenPokemon.setImageURI(uri)
        }
    }
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun configurarBotonImageViewAccesoGaleria() {
        //Cuando le da click en la imagen para guardar imagen del juego
        binding.ivImagenPokemon.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
    }

    suspend fun guardarImagenCover(stoRef: StorageReference, id:String, imagen: Uri):String{

        val urlCoverFirebase: Uri = stoRef.child("Pokemon").child("Foto").child(id)
            .putFile(imagen).await().storage.downloadUrl.await()

        return urlCoverFirebase.toString()
    }

    fun escribirPokemon(
        dbRef: DatabaseReference,
        id: String,
        nombrePokemon: String,
        naturalezaPokemon: String,
        urlFirebase: String,
        nivel: Int
    )=
        dbRef.child("Pokemon").child("Coleccion").child(id).setValue(
            PokemonColeccion(
        nombrePokemon,
        naturalezaPokemon,
        id,
        nivel,
        urlFirebase
    ))
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}