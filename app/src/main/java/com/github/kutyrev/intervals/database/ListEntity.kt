package com.github.kutyrev.intervals.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ListEntity (

        var name:String?,
        var withoutSeconds : Boolean

    ) : Parcelable{
   /* companion object {
        fun serialize(listEntities:List<ListEntity>) : Data{
            val ids = IntArray(listEntities.size) { i -> listEntities[i].id }
            val names = Array(listEntities.size) { i -> listEntities[i].name }

            return workDataOf("ids" to ids,
                   "names" to names )

        }

        fun deserialize(workData: Data): List<ListEntity>{
         val listEntities:MutableList<ListEntity> = mutableListOf<ListEntity>()
            val namesList : Array<out String>? = workData.getStringArray("names")
            val idsList : IntArray? = workData.getIntArray("ids")



                if (namesList != null) {
                    for (i in namesList.indices) {
                       // listEntities.add(Lis)
                    }
                }

            return listEntities
        }
    }*/

    @PrimaryKey(autoGenerate = true) var id:Int = 0

    constructor(parcel: Parcel) : this(parcel.readString(),
        parcel.readInt()==1
    ) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(if (withoutSeconds) 1 else 0)
        parcel.writeInt(id)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListEntity> {
        override fun createFromParcel(parcel: Parcel): ListEntity {
            return ListEntity(parcel)
        }

        override fun newArray(size: Int): Array<ListEntity?> {
            return arrayOfNulls(size)
        }
    }
}
