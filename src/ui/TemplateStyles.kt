package com.limemedia.ui

import kotlinx.css.*

fun CSSBuilder.createStyles(){
    body{
        margin(20.px)
    }

    rule("h1"){
        color = Color.red
        textAlign = TextAlign.center
    }

    rule("table"){
        alignContent = Align.center
        borderCollapse = BorderCollapse.separate
        borderSpacing = 10.px
    }

    rule("td"){
        padding(10.px, 0.px)
    }

    rule(".center"){
        marginLeft = LinearDimension.auto
        marginRight = LinearDimension.auto
    }

    rule("li"){
        display = Display.inline
    }
}



