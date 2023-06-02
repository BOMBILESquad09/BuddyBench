package it.polito.mad.buddybench.enums

enum class Visibilities {
    PRIVATE, PUBLIC, ON_REQUEST;

    companion object {

        fun fromStringToVisibility(visibility: String): Visibilities? {

            return when(visibility.lowercase()) {
                "private" -> PRIVATE
                "public" -> PUBLIC
                "on_request" -> ON_REQUEST
                else -> null
            }
        }
    }
}
