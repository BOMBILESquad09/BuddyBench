package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.persistence.entities.Court

class CourtDTO (

) {

    constructor(name: String, address: String, location: String, feeHour: Int, sport: String, phoneNumber: String,
                path: String, feeEquipment: Int, rating: Double, nReviews: Int, facilities: List<String>?
                ) : this() {
        this.name = name
        this.address = address
        this.location = location
        this.feeHour = feeHour
        this.sport = sport
        this.phoneNumber = phoneNumber
        this.path = path
        this.feeEquipment = feeEquipment
        this.rating = rating
        this.nReviews = nReviews
        this.facilities = facilities
    }

    lateinit var  name: String
    lateinit var address: String
    lateinit var location: String
    var feeHour: Int = 0
    lateinit var sport: String
    lateinit var phoneNumber: String
    lateinit var path: String
    var feeEquipment: Int = 0
    var rating: Double = 0.0
    var nReviews: Int = 0
    var facilities: List<String>? = null
    var timetable: HashMap<String, DayTimeTable> = hashMapOf()
    fun toEntity(): Court {
        return Court(

            name = this.name,
            address = this.address,
            location = this.location,
            feeHour = this.feeHour,
            sport = this.sport,
            phoneNumber = this.phoneNumber,
            path = this.path,
            feeEquipment = this.feeEquipment,
            rating = this.rating,
            nReviews = this.nReviews

        )
    }

    fun copy(): CourtDTO{
        return CourtDTO(name, address, location, feeHour, sport, phoneNumber, path, feeEquipment, rating, nReviews, facilities)
    }

    fun getId(): String{
        return this.name.replace(" ","_") + "_" + this.sport
    }


}




