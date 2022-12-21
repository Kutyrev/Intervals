package com.github.kutyrev.intervals.datasource.database

import androidx.room.Embedded
import androidx.room.Relation


data class ListAndEvent(
    @Embedded var list: ListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "listId"
    )
    var event: EventEntity
)
