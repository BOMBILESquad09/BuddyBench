package it.polito.mad.buddybench.classes

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Print {

    @Inject
    constructor(){
        println("okkkkkkk")
    }
}