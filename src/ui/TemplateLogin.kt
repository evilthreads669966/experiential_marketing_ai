package com.limemedia.ui


import io.ktor.html.*
import kotlinx.html.*
import user.account.UserAuth

class ClientForm: Template<FlowContent>{
    override fun FlowContent.apply() {
        postForm(action = "/user/clients/create", FormEncType.applicationXWwwFormUrlEncoded){
            label { +"Client Name"
                textInput(name = "name")
            }
            submitInput(name = "submit")
        }
    }
}

fun FlowContent.loginForm(){
    postForm {
        textInput {
            name = UserAuth.FORM_USERNAME
        }
        passwordInput {
            name = UserAuth.FORM_PASSWORD
        }
        submitInput{value = "Log in"}
    }
}