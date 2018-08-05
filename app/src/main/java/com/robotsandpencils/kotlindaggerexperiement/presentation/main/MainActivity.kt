package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import androidx.lifecycle.Observer
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlindaggerexperiement.app.extensions.initializeWithLinearLayout
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BaseActivity
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), Contract.View {

    companion object {
        val CURRENT_TAB_ITEM: String = "CurrentTabItem"
    }

    @Inject
    lateinit var presenter: Contract.Presenter

    private val groupAdapter = GroupAdapter<ViewHolder>()
    private val updatingGroup = Section()
    private var currentTabItem: Int = R.id.navigation_home

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        currentTabItem = item.itemId
        return@OnNavigationItemSelectedListener presenter.navigate(item.itemId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.attach(this)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (savedInstanceState != null) {
            currentTabItem = savedInstanceState.getInt(CURRENT_TAB_ITEM)
            navigation.selectedItemId = currentTabItem
        } else {
            showHome()
            hideDashboard()
            hideNotifications()
        }

        connectView()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(CURRENT_TAB_ITEM, currentTabItem)
    }

    private fun connectView() {
        connectButton()
        connectRecyclerView()
    }

    private fun connectButton() {
        button.setOnClickListener { _ ->
            Log.d("Button", "${idNumber.text} ${firstName.text} ${lastName.text}")

            // Tell the presenter to perform the database insert
            presenter.addUser(idNumber.text.toString(), firstName.text.toString(), lastName.text.toString())
        }
    }

    private fun connectRecyclerView() {

        list.initializeWithLinearLayout {
            adapter = groupAdapter
        }

        groupAdapter.add(updatingGroup)

        getViewModel()?.let { vm ->
            vm.users.observe(this, Observer { users ->
                Log.d("USERS", "Got some users: $users thread =  ${Thread.currentThread().name}")

                updatingGroup.update(getUserItems(users))
            })
        }

        groupAdapter.apply {
            setOnItemClickListener { item, _ ->
                presenter.removeUser((item as UserItem).user)
            }
        }
    }

    override fun showError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getUserItems(users: List<UserEntity>?): List<Item<ViewHolder>> {
        val items = ArrayList<UserItem>()

        users?.forEach { user ->
            items.add(UserItem(user))
        }

        return items
    }

    override fun clearFields() {
        idNumber.requestFocus()
        arrayOf(idNumber.text, firstName.text, lastName.text)
                .forEach { it.clear() }
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun setTitle(text: String) {
        message.text = text
    }

    override fun setTitle(text: Int) {
        message.text = getString(text)
    }

    override fun getViewModel(): MainViewModel? = safeGetViewModel()

    override fun showHome() {
        homeLayout.visibility = View.VISIBLE
    }

    override fun showDashboard() {
        dashboardLayout.visibility = View.VISIBLE
    }

    override fun showNotifications() {
        notificationsLayout.visibility = View.VISIBLE
    }

    override fun hideHome() {
        homeLayout.visibility = View.GONE
    }

    override fun hideDashboard() {
        dashboardLayout.visibility = View.GONE
    }

    override fun hideNotifications() {
        notificationsLayout.visibility = View.GONE
    }
}
