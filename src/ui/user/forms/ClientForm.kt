package com.limemedia.ui.user.forms

import com.limemedia.user.repository.persistence.data.Advertisement
import kotlinx.html.*


fun FlowContent.adForm(ad: Advertisement? = null){
    var action: String
    if(ad == null){
        h3 { +"Create an ad" }
        action = "/user/ads"
    }
    else{
        h3 { +"Edit Ad" }
        action = "/user/ads/edit/${ad.id}"
    }

    postForm(action, FormEncType.multipartFormData){
        name = "ad-form"
        label { +"Name"
            textInput(name = "name"){
                required = true
                if(ad != null)
                    value = ad.name
            }
        }
        br {  }
        label { +"Description"
            textInput(name = "description"){
                required = true
                if(ad != null)
                    value = ad.description
            }
        }
        br {  }
        label { +"Client"
            textInput(name = "client"){
                required = true
                if(ad != null)
                    value = ad.clientId
            }
        }
        br {  }
        label {
            +"Starting At"
            dateTimeInput(name = "start") {
                if(ad != null && ad.startDate != null)
                    value = "${ad.startDate}"
            }
        }
        br {  }
        label {
            +"Ending At"
            dateTimeInput(name = "end") {
                if(ad != null && ad.endDate != null)
                    value = "${ad.endDate}"
            }
        }
        br {  }
        label { +"Video"
            fileInput(name = "video"){
                required = true
            }
        }
        br {  }
        submitInput(name = "submit")
    }
}