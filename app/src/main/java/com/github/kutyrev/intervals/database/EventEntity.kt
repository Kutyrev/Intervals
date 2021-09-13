package com.github.kutyrev.intervals.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = [ForeignKey(
    entity = ListEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("listId"),
    onDelete = ForeignKey.CASCADE
    /*A "CASCADE" action propagates the delete or update operation on the parent key to each dependent child key.
    For onDelete() action, this means that each row in the child entity that was associated with the deleted parent row is also deleted.*/
)]
)
data class EventEntity (


    var listId:Int,
    var dateStamp:Calendar?,
    var comment:String?

){
    @PrimaryKey (autoGenerate = true) var id:Int = 0

}