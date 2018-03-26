package com.romrell4.bracketchallenge.controller

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.MatchHelper
import com.romrell4.bracketchallenge.model.Tournament
import kotlinx.android.synthetic.main.row_match.view.*

/**
 * Created by romrell4 on 3/25/18
 */
class MyBracketFragment: Fragment() {
    companion object {
        private const val TOURNAMENT_EXTRA = "tournament"

        fun newInstance(tournament: Tournament): MyBracketFragment {
            val fragment = MyBracketFragment()
            val bundle = Bundle()
            bundle.putParcelable(TOURNAMENT_EXTRA, tournament)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val adapter = MatchAdapter()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_my_bracket, container, false)



        return view
    }

    inner class MatchAdapter(var matches: List<MatchHelper> = emptyList()): RecyclerView.Adapter<MatchAdapter.ViewHolder>() {
        override fun getItemCount() = matches.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(activity.layoutInflater.inflate(R.layout.row_match, parent, false))
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(matches[position])

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            private val nameTextView1 = view.nameTextView1
            private val checkmark1 = view.checkmark1
            private val nameTextView2 = view.nameTextView2
            private val checkmark2 = view.checkmark2

            fun bind(match: MatchHelper) {
                nameTextView1.text = match.player1Full
                checkmark1.visibility = if (match.player1Id == match.winnerId) View.VISIBLE else View.GONE
                nameTextView2.text = match.player2Full
                checkmark2.visibility = if (match.player2Id == match.winnerId) View.VISIBLE else View.GONE
            }
        }
    }
}