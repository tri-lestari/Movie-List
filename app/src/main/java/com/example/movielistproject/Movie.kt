package com.example.movielistproject
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.nio.channels.spi.AbstractSelectionKey

@IgnoreExtraProperties
data class Movie(val name: String? = null, val date: String? = null,  val description: String? = null, val url: String? = null,
                 @Exclude val key: String? = null){
}