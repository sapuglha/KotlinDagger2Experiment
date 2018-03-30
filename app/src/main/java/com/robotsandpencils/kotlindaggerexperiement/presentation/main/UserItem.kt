package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_user.view.*

class UserItem(internal val user: UserEntity) : Item<ViewHolder>(user.uid.toLong()) {

    override fun getLayout(): Int {
        return R.layout.item_user
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            firstName.text = user.firstName
            lastName.text = user.lastName
            idNumber.text = user.uid.toString()
        }
    }
}