package it.polito.mad.buddybench.classes

import com.google.firebase.firestore.DocumentReference

class ProfileData() {

    constructor(
        name: String?,
        surname: String?,
        nickname: String?,
        email: String,
        location: String?,
        birthdate: String,
        reliability: Int,
        imageUri: String?,
        sports: MutableList<Sport>,
        friends: List<DocumentReference>,
        friend_requests_pending: List<DocumentReference>,

    ) : this() {
        this.name = name
        this.surname = surname
        this.nickname = nickname
        this.email = email
        this.location = location
        this.birthdate = birthdate
        this.reliability = reliability
        this.imageUri = imageUri
        this.sports = sports
        this.friends = friends
        this.friend_requests_pending = friend_requests_pending

    }

    var name: String? = null
    var surname: String? = null
    var nickname: String? = null
    var email: String = ""
    var location: String? = null
    var birthdate: String = ""
    var reliability: Int = 0
    var imageUri: String? = null
    var sports: MutableList<Sport> = mutableListOf()
    var friends: List<DocumentReference> = mutableListOf()
    var friend_requests_pending: List<DocumentReference> = mutableListOf()
    var play_request_pendings: List<DocumentReference> = mutableListOf()
}
