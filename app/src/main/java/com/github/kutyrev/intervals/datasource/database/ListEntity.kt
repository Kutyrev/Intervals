package com.github.kutyrev.intervals.datasource.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ListEntity(
    var name: String?,
    var withoutSeconds: Boolean
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt() == 1
    ) {
        id = parcel.readInt()
    }

    companion object CREATOR : Parcelable.Creator<ListEntity> {
        override fun createFromParcel(parcel: Parcel): ListEntity {
            return ListEntity(parcel)
        }

        override fun newArray(size: Int): Array<ListEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(if (withoutSeconds) 1 else 0)
        parcel.writeInt(id)

    }

    override fun describeContents(): Int {
        return 0
    }
}
