package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

sealed class ComicState {
    class Loading : ComicState()
    class Error : ComicState()
    data class ComicLoaded(val title: String, val imageUrl: String, val comicNumber: Int)
        : ComicState()
}