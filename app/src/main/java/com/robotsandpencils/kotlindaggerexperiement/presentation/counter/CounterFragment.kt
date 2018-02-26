package com.robotsandpencils.kotlindaggerexperiement.presentation.counter

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robotsandpencils.kotlindaggerexperiement.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_counter.*

import javax.inject.Inject

class CounterFragment : Fragment(), Contract.View {
    @Inject
    lateinit var presenter: Contract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getViewModel(): CounterViewModel {
        return ViewModelProviders.of(this).get(CounterViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attach(this)

        getViewModel().count.observe(this, Observer { count ->
            count_text.text = "$count"
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.detach()
    }
}