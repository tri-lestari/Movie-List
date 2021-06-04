package com.example.movielistproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add.descriptionTextView
import kotlinx.android.synthetic.main.activity_add.nameTextView
import kotlinx.android.synthetic.main.activity_movie_detail.*

class MovieDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("movie").child(
            key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val movie:Movie? = dataSnapshot.getValue(Movie::class.java)
                if (movie != null) {
                    nameTextView.text = movie.name.toString()
                    descriptionTextView.text = movie.description.toString()
                    images(movie.url.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private  fun images(url: String){
        Glide.with(this)
            .load(url)
            .into(posterImgeView)

        Glide.with(this)
            .load(url)
            .into(backgroundImageView)
    }
}