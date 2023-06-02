package it.polito.mad.buddybench.activities.findcourt

enum class States {
    SPORTS_SELECTION, SEARCH;
}

enum class FindStates(val value: Int) {
    COURTS(0),
    GAMES(1);
}
