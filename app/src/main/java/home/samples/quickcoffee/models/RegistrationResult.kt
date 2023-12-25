package home.samples.quickcoffee.models

data class RegistrationResult(
    val token: String,
    val tokenLifetime: Int
)