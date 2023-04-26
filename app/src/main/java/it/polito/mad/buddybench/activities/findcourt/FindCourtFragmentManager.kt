package it.polito.mad.buddybench.activities.findcourt

import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.findcourt.sportselection.SportsSelectionFragment

class FindCourtFragmentManager(private val root: FindCourtFragment) {
    private var currentState = States.SPORTS_SELECTION
    private val context = root.context

    fun setup(){
        replaceFragment(currentState, currentState)
    }

    fun switchFragment(newState: States){
        replaceFragment(currentState, newState)
        currentState = newState
    }


    private fun replaceFragment(lastTag: States, newTag: States){
        val transaction = context.supportFragmentManager.beginTransaction()

        context.supportFragmentManager.findFragmentByTag(lastTag.name).let {
            if(it != null){
                transaction.hide(it)
            }
        }

        context.supportFragmentManager.findFragmentByTag(newTag.name).let {
            if (it == null){
                val newFragment =  when(newTag){
                    States.SPORTS_SELECTION -> SportsSelectionFragment(root)
                    States.SEARCH -> SearchFragment(root)
                }
                transaction.add(R.id.find_court, newFragment, newTag.name)
            } else {
                transaction.show(it)
            }
        }
        transaction.commit()
    }

}