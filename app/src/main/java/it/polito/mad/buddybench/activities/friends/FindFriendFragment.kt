package it.polito.mad.buddybench.activities.friends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent
import it.polito.mad.buddybench.viewmodels.FriendsViewModel

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class FindFriendFragment : Fragment() {

    // ** UI
    private lateinit var rvFindFriends: RecyclerView
    private lateinit var pbFindFriends: LinearLayout
    private lateinit var emptyLL: LinearLayout
    private lateinit var emptyTv: TextView
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var searchEditText: EditText

    // ** View Model
    private val friendsViewModel by activityViewModels<FriendsViewModel>()

    // ** Column count
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_friends_list, container, false)
        rvFindFriends = view.findViewById(R.id.rv_find_friends)
        pbFindFriends = view.findViewById(R.id.pb_find_friends)
        emptyLL = view.findViewById(R.id.empty)
        emptyTv = view.findViewById(R.id.tv_empty)
        searchEditText = view.findViewById(R.id.search_edit_text)
        emptyTv.text = getString(R.string.no_friends_to_find)
        swipeRefresh = view.findViewById(R.id.swiperefresh)
        swipeRefresh
        swipeRefresh.setOnRefreshListener {
            (activity as HomeActivity).friendsViewModel.refreshAll{swipeRefresh.isRefreshing = false}
        }
        // ** Loading state
        friendsViewModel.lPossible.observe(viewLifecycleOwner) {
            println(it ?: null)
            if (it == null || it) {
                pbFindFriends.visibility = View.VISIBLE

            } else {
                pbFindFriends.visibility = View.GONE
            }
        }
        with(rvFindFriends) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = FindFriendRecyclerViewAdapter(listOf(), friendsViewModel)

        }


        // ** Data
        friendsViewModel.possibleFriends.observe(viewLifecycleOwner) {
            if (it != null) {
                with(rvFindFriends) {
                    val oldList = (adapter as FindFriendRecyclerViewAdapter).values
                    val friendDiff = FriendListDiffUtils(oldList, it)
                    (adapter as FindFriendRecyclerViewAdapter).values = it
                    val diffs = DiffUtil.calculateDiff(friendDiff)
                    diffs.dispatchUpdatesTo(adapter!!)
                    if((rvFindFriends.adapter as FindFriendRecyclerViewAdapter).values.isEmpty()){
                        emptyLL.visibility = View.VISIBLE
                        rvFindFriends.visibility = View.GONE
                    } else{
                        emptyLL.visibility = View.GONE
                        rvFindFriends.visibility = View.VISIBLE
                    }
                }
            }

            searchEditText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    friendsViewModel.searchText = s.toString().trim().replace("\\s+".toRegex(), " ")
                    friendsViewModel.applyFilter()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }









        return view
    }
}