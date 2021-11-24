package com.limemedia.ui


import io.ktor.html.*
import kotlinx.html.*

class ArticleTemplate: Template<FlowContent>{
    val heading = Placeholder<FlowContent>()
    val content = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        article{
            header {
                h2 { insert(heading) }
            }
            insert(content)
        }
    }
}

class FormListTemplate(private val columns: DoubleColumnTemplate = DoubleColumnTemplate()): Template<HTML>{
    val article = TemplatePlaceholder<ArticleTemplate>()
    val formContent = Placeholder<FlowContent>()

    override fun HTML.apply() {
        insert(columns){
            columnOne{
                insert(formContent)
            }
            columnTwo{
                aside {
                    insert(ArticleTemplate(), article)
                }
            }
        }
    }
}

class SingleColumnTemplate(private val main: MainTemplate = MainTemplate()): Template<HTML>{
    val column = Placeholder<FlowContent>()
    override fun HTML.apply() {
        insert(main){
            content{
                insert(column)
            }
        }
    }
}

class DoubleColumnTemplate(private val main: MainTemplate = MainTemplate()): Template<HTML>{
    val columnOne = Placeholder<FlowContent>()
    val columnTwo = Placeholder<FlowContent>()

    override fun HTML.apply() {
        insert(main){
            content{
                div(classes = "column") {
                    id = "col-1"
                    insert(columnOne)
                }
                div(classes = "column") {
                    id = "col-2"
                    insert(columnTwo)
                }
            }
/*                footerMenu{
                    item{ a(href = "/user/clients") { +"Clients" }}
                    item{ a(href = "/user/advertisements") { +"Advertisements" }}
                    item{ a(href = "/logout") { +"Logout" }}
                }*/
        }
    }
}

class MainTemplate: Template<HTML>{
    val heading = Placeholder<FlowContent>()
    val content = Placeholder<HtmlBlockTag>()

    override fun HTML.apply() {
        head {
            link(rel = "stylesheet", href = "/styles.css", type = "text/css")
            title { +"Experential Marketing" }
        }
        body {
            header {
                h1 { insert(heading) }
                nav(classes = "navmenu") {
                    id = "nav-header"
                    /*        insert(MenuTemplate()){
                                    item{ a(href = "/user/clients") { +"Clients" }}
                                    item{ a(href = "/user/advertisements") { +"Advertisements" }}
                                    item{ a(href = "/logout") { +"Logout" }}
                            }*/
                }
            }
            insert(content)
/*                footer{
                    nav(classes = "navmenu") {
                        id = "nav-footer"
                        insert(MenuTemplate(), footerMenu)
                    }
                }*/
        }
    }
}

class MenuTemplate: Template<FlowContent> {
    val item = PlaceholderList<UL, FlowContent>()

    override fun FlowContent.apply() {
        if(!item.isEmpty()){
            ul {
                each(item){
                    item{ insert(it) }
                }
            }
        }
    }
}
