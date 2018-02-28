package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robotsandpencils.kotlindaggerexperiement.R
import com.squareup.picasso.Picasso
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_comic.*
import javax.inject.Inject

class ComicFragment : Fragment(), Contract.View {
    @Inject
    lateinit var presenter: Contract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getViewModel(): ComicViewModel {
        return ViewModelProviders.of(this).get(ComicViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attach(this)

        getViewModel().apply {
            state.observe(this@ComicFragment, Observer {
                it?.let {

                    TransitionManager.beginDelayedTransition(comicFragment, Fade())

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

        previousButton.setOnClickListener {
            presenter.showPreviousComic()
        }
    }

    private fun renderLoading() {
        titleText.text = "Loading..."
        imageView.setImageDrawable(null)
    }

    private fun renderError() {
        titleText.text = "Error!"
        imageView.setImageDrawable(null)
    }

    private fun renderComic(state: ComicState.ComicLoaded) {
        Picasso.with(imageView.context).load(state.imageUrl).into(imageView)
        imageView.tag = state.imageUrl
        titleText.text = state.title
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.detach()
    }
}