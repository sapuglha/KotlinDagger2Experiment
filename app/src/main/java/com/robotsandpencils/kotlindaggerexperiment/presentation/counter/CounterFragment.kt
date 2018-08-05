package com.robotsandpencils.kotlindaggerexperiment.presentation.counter

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robotsandpencils.kotlindaggerexperiment.R
import com.robotsandpencils.kotlindaggerexperiment.presentation.base.BaseFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_counter.*
import javax.inject.Inject

class CounterFragment : BaseFragment(), Contract.View {
    @Inject
    lateinit var presenter: Contract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getViewModel(): CounterViewModel? = safeGetViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attach(this)

        getViewModel()?.let { vm ->
            vm.count.observe(this, Observer { count ->
                count_text.text = "$count"
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.detach()
    }
}