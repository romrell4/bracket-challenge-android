package com.romrell4.bracketchallenge.controller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Client
import com.romrell4.bracketchallenge.model.Tournament
import kotlinx.android.synthetic.main.fragment_standings.view.*
import kotlinx.android.synthetic.main.row_user_score.view.*
import retrofit2.Response

/**
 * Created by romrell4 on 3/25/18
 */
class StandingsFragment: Fragment() {
	companion object {
		private const val TOURNAMENT_EXTRA = "tournament"

		fun newInstance(tournament: Tournament): StandingsFragment {
			val fragment = StandingsFragment()
			val bundle = Bundle()
			bundle.putParcelable(TOURNAMENT_EXTRA, tournament)
			fragment.arguments = bundle
			return fragment
		}
	}

	private lateinit var tournament: Tournament
	private val adapter = BracketAdapter()
	private lateinit var refreshControl: SwipeRefreshLayout

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_standings, container, false)

		view?.run {
			recyclerView.let {
				it.layoutManager = LinearLayoutManager(activity)
				it.adapter = adapter
			}

			tournament = arguments?.getParcelable(TOURNAMENT_EXTRA) ?: throw IllegalStateException()
			refreshControl = swipeRefreshLayout.also { it.setOnRefreshListener { loadData() } }
			loadData()
		}

		return view
	}

	private fun loadData() {
		refreshControl.isRefreshing = true
		Client.createApi().getBrackets(tournament.tournamentId).enqueue(object: Client.SimpleCallback<List<Bracket>>(requireActivity()) {
			override fun onResponse(data: List<Bracket>?, errorResponse: Response<List<Bracket>>?) {
				refreshControl.isRefreshing = false
				data?.let {
					adapter.brackets = data.filter { it.bracketId != tournament.masterBracketId }
					adapter.notifyDataSetChanged()
				}
			}
		})
	}

	inner class BracketAdapter(var brackets: List<Bracket> = emptyList()): RecyclerView.Adapter<BracketAdapter.ViewHolder>() {
		override fun getItemCount() = brackets.size
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(layoutInflater.inflate(R.layout.row_user_score, parent, false))
		override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(brackets[position])

		inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
			private val nameTextView = view.nameTextView
			private val scoreTextView = view.scoreTextView

			fun bind(bracket: Bracket) {
				nameTextView.text = bracket.name
				scoreTextView.text = bracket.score.toString()

				itemView.setOnClickListener {
					startActivity(Intent(activity, UserBracketActivity::class.java)
							.putExtra(UserBracketActivity.TOURNAMENT_EXTRA, tournament)
							.putExtra(UserBracketActivity.BRACKET_EXTRA, bracket))
				}
			}
		}
	}
}