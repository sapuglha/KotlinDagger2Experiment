package com.robotsandpencils.kotlindaggerexperiement.domain.comic

import com.robotsandpencils.kotlindaggerexperiement.net.xkcd.XkcdResponse
import org.joda.time.LocalDate

fun XkcdResponse.toDomain(): Comic =
        Comic(this.img, this.title, this.num,
                LocalDate(this.year.toInt(), this.month.toInt(), this.day.toInt()))