package it.polito.mad.buddybench.classes

data class ProfileData
    (var name: String?,
     var surname: String?,
     var nickname: String?,
     var email: String,
     var location: String?,
     var birthdate: String,
     var reliability: Int,
     var imageUri: String?,
     var sports: MutableList<Sport> )
