package com.example.movielistproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.movie_content.view.*

class MainActivity : AppCompatActivity() {
    private val database = Firebase.database
    private lateinit var messagesListener: ValueEventListener
    private val listMovie:MutableList<Movie> = ArrayList()
    val myRef = database.getReference("movie")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newFloatingActionButton.setOnClickListener { v ->
            val intent = Intent(this, AddActivity::class.java)
            v.context.startActivity(intent)
        }
        listMovie.clear()
        setupRecyclerView(MovieRecyclerView)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        messagesListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listMovie.clear()
                dataSnapshot.children.forEach { child ->
                    val movie: Movie? =
                        Movie(child.child("name").getValue<String>(),
                              child.child("date").getValue<String>(),
                              child.child("description").getValue<String>(),
                              child.child("url").getValue<String>(),
                              child.key)
                        movie?.let { listMovie.add(it) }
                }
                recyclerView.adapter = MovieViewAdapter(listMovie)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)

        deleteSwipe(recyclerView)
    }

    class MovieViewAdapter(private val values: List<Movie>) :
        RecyclerView.Adapter<MovieViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val movie = values[position]
            holder.mNameTextView.text = movie.name
            holder.mDateTextView.text = movie.date
            holder.mPosterImgeView?.let {
                Glide.with(holder.itemView.context)
                    .load(movie.url)
                    .into(it)
            }

            holder.itemView.setOnClickListener { v ->
                val intent = Intent(v.context, MovieDetail::class.java).apply {
                    putExtra("key", movie.key)
                }
                v.context.startActivity(intent)
            }

            holder.itemView.setOnLongClickListener{ v ->
                val intent = Intent(v.context, EditActivity::class.java).apply {
                    putExtra("key", movie.key)
                }
                v.context.startActivity(intent)
                true
            }

        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mNameTextView: TextView = view.nameTextView
            val mDateTextView: TextView = view.dateTextView
            val mPosterImgeView: ImageView? = view.posterImgeView
        }
    }

    private fun deleteSwipe(recyclerView: RecyclerView){
        val touchHelperCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listMovie.get(viewHolder.adapterPosition).key?.let { myRef.child(it).setValue(null) }
                listMovie.removeAt(viewHolder.adapterPosition)
                recyclerView.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}