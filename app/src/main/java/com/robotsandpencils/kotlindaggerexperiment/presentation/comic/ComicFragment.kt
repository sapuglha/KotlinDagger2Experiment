package com.robotsandpencils.kotlindaggerexperiment.presentation.comic

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robotsandpencils.kotlindaggerexperiment.R
import com.robotsandpencils.kotlindaggerexperiment.presentation.base.BaseFragment
import com.robotsandpencils.kotlindaggerexperiment.presentation.base.PresenterLifecycleListener
import com.squareup.picasso.Picasso
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_comic.*
import javax.inject.Inject

class ComicFragment : BaseFragment(), Contract.View {
    @Inject
    lateinit var presenter: Contract.Presenter

    private var presenterLifecycleListener: PresenterLifecycleListener<Contract.View, Contract.Presenter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getViewModel(): ComicViewModel? =
            safeGetViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenterLifecycleListener = PresenterLifecycleListener(lifecycle, this, presenter)

        getViewModel()?.apply {
            state.observe(this@ComicFragment, Observer {
                it?.let {
                    when (it) {
                        is ComicState.Loading -> {
                            renderLoading()
                        }
                        is ComicState.Error -> {
                            renderError()
                        }
                        is ComicState.ComicLoaded -> renderComic(it)
                    }
                }
            })
        }

        previousButton?.setOnClickListener {
            presenter.showPreviousComic()
        }

        nextButton?.setOnClickListener {
            presenter.showNextComic()
        }
    }

    private fun renderLoading() {
        titleText.text = getString(R.string.comic_loading)
        imageView.setImageDrawable(null)
        imageView.visibility = View.GONE
    }

    private fun renderError() {
        titleText.text = getString(R.string.comic_error)
        imageView.setImageDrawable(null)
        imageView.visibility = View.GONE
    }

    private fun renderComic(state: ComicState.ComicLoaded) {
        Picasso.with(imageView.context).load(state.imageUrl).into(imageView)
        imageView.tag = state.imageUrl
        titleText.text = state.title
        imageView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenterLifecycleListener = null
    }
}