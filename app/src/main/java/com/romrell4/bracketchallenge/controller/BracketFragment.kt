package com.romrell4.bracketchallenge.controller

import android.app.Fragment
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Match
import com.romrell4.bracketchallenge.model.Tournament
import kotlinx.android.synthetic.main.fragment_bracket.*
import kotlinx.android.synthetic.main.row_match.view.*
import kotlin.math.pow

abstract class BracketFragment: Fragment() {
    companion object {
        private const val TOURNAMENT_EXTRA = "tournament"
        private const val VIEW_BRACKET_INDEX = 0
        private const val VIEW_BRACKET_ROUNDS_INDEX = 1
    }

    //Properties
    protected lateinit var tournament: Tournament
    protected var bracket: Bracket? = null
        set(value) {
            field = value
            value?.let { setupViewBracketUI() }
        }

    //Abstract functions
    abstract val areCellClickable: Boolean

    //Setup functions
    protected fun setArguments(tournament: Tournament) {
        val bundle = Bundle()
        bundle.putParcelable(TOURNAMENT_EXTRA, tournament)
        arguments = bundle
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = inflater?.inflate(R.layout.fragment_bracket, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        tournament = arguments.getParcelable(TOURNAMENT_EXTRA)
    }

    //UI Functions

    private fun setupViewBracketUI() {
        viewSwitcher.displayedChild = VIEW_BRACKET_INDEX
        matchesViewSwitcher.displayedChild = VIEW_BRACKET_ROUNDS_INDEX

        viewPager.adapter = RoundPagerAdapter(bracket?.rounds ?: emptyList())
    }

    inner class RoundPagerAdapter(private val rounds: List<List<Match>>): PagerAdapter() {
        override fun getCount() = rounds.size
        override fun isViewFromObject(view: View, `object`: Any) = view == `object`
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as? View)

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val roundView = activity.layoutInflater.inflate(R.layout.view_round, container, false)
            (roundView as? RecyclerView?)?.run {
                layoutManager = LinearLayoutManager(activity)
                adapter = MatchAdapter(position, rounds[position])
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
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.run {
                    val cardHeight = activity.resources.getDimension(R.dimen.match_card_height)
                    val cardMargin = activity.resources.getDimension(R.dimen.match_card_margin)
                    val newMargin = ((cardHeight / 2 + cardMargin) * (2.0.pow(round) - 1) + cardMargin).toInt()
                    topMargin = newMargin
                    bottomMargin = newMargin
                    itemView.requestLayout()
                }
                nameTextView1.text = match.player1Full()
                checkmark1.visibility = if (match.player1Id != null && match.player1Id == match.winnerId) View.VISIBLE else View.GONE
                nameTextView2.text = match.player2Full()
                checkmark2.visibility = if (match.player2Id != null && match.player2Id == match.winnerId) View.VISIBLE else View.GONE
            }
        }
    }
}