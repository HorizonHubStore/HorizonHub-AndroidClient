package com.example.horizonhub_androidclient.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.data.gamePost.GamePost
import com.google.firebase.database.FirebaseDatabase

class EditPostFragment : Fragment(R.layout.fragment_edit_post) {

    private lateinit var gamePost: GamePost
    private lateinit var editTextGameName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var btnEdit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_post, container, false)

        editTextGameName = view.findViewById(R.id.editTextGameName)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextPrice = view.findViewById(R.id.editTextPrice)
        btnEdit = view.findViewById(R.id.btnEdit)

        arguments?.let {
            gamePost = it.getParcelable("gamePost")!!
        }

        editTextGameName.setText(gamePost.gameName)
        editTextDescription.setText(gamePost.description)
        editTextPrice.setText(gamePost.price.toString())

        btnEdit.setOnClickListener {
            val newGameName = editTextGameName.text.toString()
            val newDescription = editTextDescription.text.toString()
            val newPrice = editTextPrice.text.toString().toLong()

            val updatedGamePost = GamePost(
                id = gamePost.id,
                creator = gamePost.creator,
                gameImage = gamePost.gameImage,
                gameName = newGameName,
                description = newDescription,
                price = newPrice
            )

            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("games").child(updatedGamePost.id)
            reference.setValue(updatedGamePost)

            activity?.onBackPressed()
        }

        return view
    }
}