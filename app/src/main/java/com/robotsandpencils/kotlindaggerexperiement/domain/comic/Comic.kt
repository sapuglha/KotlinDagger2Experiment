package com.robotsandpencils.kotlindaggerexperiement.domain.comic

import org.joda.time.LocalDate

data class Comic(val img: String,
                 val title: String,
                 val number: Int,
                 val date: LocalDate)