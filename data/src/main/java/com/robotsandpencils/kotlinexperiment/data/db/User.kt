package com.robotsandpencils.kotlinexperiment.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity

@Entity data class User(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String)

fun User.toDomain(): UserEntity {
    return UserEntity(this.uid, this.firstName, this.lastName)
}

fun UserEntity.toData(): User {
    return User(this.uid, this.firstName, this.lastName)
}