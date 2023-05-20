package it.polito.mad.buddybench.enums

enum class Tabs {

    PROFILE, RESERVATIONS, FINDCOURT, FRIENDS;

    fun getId(): Int{
        return when(this){
            FINDCOURT -> 0
            RESERVATIONS -> 1
            PROFILE -> 2
            FRIENDS -> 3
        }
    }
}