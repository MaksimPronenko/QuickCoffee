package home.samples.quickcoffee.models

import com.google.gson.annotations.SerializedName

data class RegistrationData(
    @SerializedName("login") val email: String,
    @SerializedName("password") val password: String
)