package it.polito.mad.buddybench.persistence.dto

 class DayTimeTable() {

     constructor(openingTime: Long, closingTime: Long): this(){
         this.openingTime = openingTime
         this.closingTime = closingTime
     }
     var openingTime: Long = 0
     var closingTime: Long = 0
}