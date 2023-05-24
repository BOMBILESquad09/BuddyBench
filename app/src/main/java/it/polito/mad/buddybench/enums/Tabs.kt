package it.polito.mad.buddybench.enums

enum class Tabs {

    PROFILE, RESERVATIONS, INVITATIONS, FIND, FRIENDS;

    fun getId(): Int{
        return when(this){
            FIND -> 0
            RESERVATIONS -> 1
            FRIENDS -> 2
            INVITATIONS -> 3
            PROFILE -> 4
        }
    }
}