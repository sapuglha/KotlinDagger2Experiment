package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
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

        presenter.detach()
    }
}