package com.romrell4.bracketchallenge.controller

import android.app.Fragment
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Client
import com.romrell4.bracketchallenge.model.Match
import com.romrell4.bracketchallenge.model.Tournament
import com.romrell4.bracketchallenge.support.showLoadingDialog
import com.romrell4.bracketchallenge.support.showToast
import kotlinx.android.synthetic.main.fragment_my_bracket.view.*
import kotlinx.android.synthetic.main.row_match.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by romrell4 on 3/25/18
 */
class MyBracketFragment: Fragment() {
    companion object {
        private const val TOURNAMENT_EXTRA = "tournament"
        private const val VIEW_BRACKET_INDEX = 0
        private const val CREATE_BRACKET_INDEX = 1
        private const val SPINNER_INDEX = 0
        private const val ROUNDS_INDEX = 1

        fun newInstance(tournament: Tournament): MyBracketFragment {
            val fragment = MyBracketFragment()
            val bundle = Bundle()
            bundle.putParcelable(TOURNAMENT_EXTRA, tournament)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var myView: View
    private lateinit var tournament: Tournament
    private var bracket: Bracket? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_my_bracket, container, false)

        view?.run {
            myView = this

            tournament = arguments.getParcelable(TOURNAMENT_EXTRA)
            loadBracket()
        }

        return view
    }

    private fun loadBracket() {
        myView.matchesViewSwitcher.displayedChild = SPINNER_INDEX
        Client.createApi().getMyBracket(tournament.tournamentId).enqueue(object: Callback<Bracket> {
            override fun onResponse(call: Call<Bracket>?, response: Response<Bracket>?) {
                //If we have a bracket, view it. Otherwise, view the create bracket UI
                bracket = response?.body()
                bracket?.rounds?.let {
                    setupViewBracketUI(it)
                } ?: response?.code()?.let {
                    if (it == 404) {
                        setupCreateBracketUI()
                    }
                }
            }

            override fun onFailure(call: Call<Bracket>?, t: Throwable?) {
                activity.showToast(activity.getString(R.string.error_message, t?.message), Toast.LENGTH_LONG)
            }
        })
    }

    private fun setupViewBracketUI(rounds: List<List<Match>>) {
        myView.viewSwitcher.displayedChild = VIEW_BRACKET_INDEX
        myView.matchesViewSwitcher.displayedChild = ROUNDS_INDEX

        myView.viewPager.adapter = RoundPagerAdapter(rounds)
    }

    private fun setupCreateBracketUI() {
        myView.viewSwitcher.displayedChild = CREATE_BRACKET_INDEX

        myView.createBracketButton.setOnClickListener {
            tournament.masterBracketId?.let {
                val dialog = activity.showLoadingDialog()
                Client.createApi().createBracket(it).enqueue(object: Client.SimpleCallback<Bracket>(activity) {
                    override fun onResponse(data: Bracket?, errorResponse: Response<Bracket>?) {
                        dialog.dismiss()
                    }
                })
            }
        }
    }

    inner class RoundPagerAdapter(private val rounds: List<List<Match>>): PagerAdapter() {
        override fun getCount() = rounds.size
        override fun isViewFromObject(view: View, `object`: Any) = view == `object`
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as? View)

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val roundView = activity.layoutInflater.inflate(R.layout.view_round, container, false)
            (roundView as? RecyclerView?)?.let {
                it.layoutManager = LinearLayoutManager(activity)
                it.adapter = MatchAdapter(position, rounds[position])
            }
            container.addView(roundView)
            return roundView
        }
    }

    inner class MatchAdapter(private val round: Int, private val matches: List<Match> = emptyList()): RecyclerView.Adapter<MatchAdapter.ViewHolder>() {
        override fun getItemCount() = matches.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(activity.layoutInflater.inflate(R.layout.row_match, parent, false))
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(matches[position])

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            private val nameTextView1 = view.nameTextView1
            private val checkmark1 = view.checkmark1
            private val nameTextView2 = view.nameTextView2
            private val checkmark2 = view.checkmark2

            fun bind(match: Match) {
                nameTextView1.text = match.player1Full()
                checkmark1.visibility = if (match.player1Id != null && match.player1Id == match.winnerId) View.VISIBLE else View.GONE
                nameTextView2.text = match.player2Full()
                checkmark2.visibility = if (match.player2Id != null && match.player2Id == match.winnerId) View.VISIBLE else View.GONE
            }
        }
    }
}