package com.romrell4.bracketchallenge.controller

import android.app.Fragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Client
import com.romrell4.bracketchallenge.model.Match
import com.romrell4.bracketchallenge.model.Tournament
import com.romrell4.bracketchallenge.support.visible
import kotlinx.android.synthetic.main.fragment_bracket.*
import kotlinx.android.synthetic.main.row_match.view.*
import retrofit2.Response
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
			setupViewBracketUI()
		}
	protected var masterBracket: Bracket? = null
		set(value) {
			field = value
			setupViewBracketUI()
		}

	//Overridable functions
	protected abstract val areCellsClickable: Boolean

	open protected fun getTextColor(playerId: Int?, predictionId: Int?, winnerId: Int?) = ContextCompat.getColor(activity, R.color.black)

	//Setup functions
	protected fun setArguments(tournament: Tournament) {
		val bundle = Bundle()
		bundle.putParcelable(TOURNAMENT_EXTRA, tournament)
		arguments = bundle
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		if (areCellsClickable) {
			inflater?.inflate(R.menu.save_tournament, menu)
		}
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = inflater?.inflate(R.layout.fragment_bracket, container, false)

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		tournament = arguments.getParcelable(TOURNAMENT_EXTRA)
		tournament.masterBracketId?.let {
			Client.createApi().getBracket(tournament.tournamentId, it).enqueue(object: Client.SimpleCallback<Bracket>(activity) {
				override fun onResponse(data: Bracket?, errorResponse: Response<Bracket>?) {
					data?.let {
						masterBracket = it
					}
				}
			})
		}
	}

	//UI Functions

	private fun setupViewBracketUI() {
		if (bracket != null && masterBracket != null) {
			viewSwitcher.displayedChild = VIEW_BRACKET_INDEX
			matchesViewSwitcher.displayedChild = VIEW_BRACKET_ROUNDS_INDEX

			//Tell the view pager to keep all pages in memory (so that we can scroll between them)
			bracket?.rounds?.let { viewPager.offscreenPageLimit = it.size - 1 }
			viewPager.adapter = RoundPagerAdapter(bracket, masterBracket)
		}
	}

	inner class RoundPagerAdapter(private val bracket: Bracket?, private val masterBracket: Bracket?): PagerAdapter() {
		private var recyclerViews = arrayOfNulls<RecyclerView>(count)
		private var scrollListeners = arrayOfNulls<RecyclerView.OnScrollListener>(count)

		override fun getCount() = bracket?.rounds?.size ?: 0
		override fun isViewFromObject(view: View, `object`: Any) = view == `object`
		override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as? View)

		override fun instantiateItem(container: ViewGroup, position: Int): Any {
			val roundView = activity.layoutInflater.inflate(R.layout.view_round, container, false)
			(roundView as? RecyclerView?)?.apply {
				layoutManager = LinearLayoutManager(activity)
				adapter = MatchAdapter(position, bracket?.rounds?.get(position) ?: emptyList(), masterBracket?.rounds?.get(position) ?: emptyList())

				recyclerViews[position] = this
				scrollListeners[position] = object: RecyclerView.OnScrollListener() {
					override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
						super.onScrolled(recyclerView, dx, dy)
						recyclerViews.zip(scrollListeners)
								.filter { it.first != recyclerView }
								.map { it.first?.removeOnScrollListener(it.second); it }
								.map { it.first?.scrollBy(dx, dy); it }
								.forEach { it.first?.addOnScrollListener(it.second) }
					}
				}.also { addOnScrollListener(it) }
			}
			container.addView(roundView)
			return roundView
		}
	}

	inner class MatchAdapter(private val round: Int, private val matches: List<Match>, private val masterMatches: List<Match>): RecyclerView.Adapter<MatchAdapter.ViewHolder>() {
		override fun getItemCount() = matches.size
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(activity.layoutInflater.inflate(R.layout.row_match, parent, false))
		override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(matches[position], masterMatches[position])

		inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
			private val player1Layout = view.player1Layout
			private val player2Layout = view.player2Layout
			private val nameTextView1 = view.nameTextView1
			private val checkmark1 = view.checkmark1
			private val nameTextView2 = view.nameTextView2
			private val checkmark2 = view.checkmark2

			fun bind(match: Match, masterMatch: Match) {
				//Calculate the correct margin around the cell
				(itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.run {
					val cardHeight = activity.resources.getDimension(R.dimen.match_card_height)
					val cardMargin = activity.resources.getDimension(R.dimen.match_card_margin)
					val newMargin = ((cardHeight / 2 + cardMargin) * (2.0.pow(round) - 1) + cardMargin).toInt()
					topMargin = newMargin
					bottomMargin = newMargin
					itemView.requestLayout()
				}

				//Set up the text and checks
				nameTextView1.text = match.player1Full()
				nameTextView1.setTextColor(getTextColor(match.player1Id, match.winnerId, masterMatch.winnerId))
				checkmark1.visibility = if (match.player1Id != null && match.player1Id == match.winnerId) View.VISIBLE else View.GONE
				nameTextView2.text = match.player2Full()
				nameTextView2.setTextColor(getTextColor(match.player2Id, match.winnerId, masterMatch.winnerId))
				checkmark2.visibility = if (match.player2Id != null && match.player2Id == match.winnerId) View.VISIBLE else View.GONE

				//Set up the click listener
				if (areCellsClickable) {
					player1Layout.setOnClickListener {
						checkmark1.visible = !checkmark1.visible
						checkmark2.visibility = View.GONE
						match.winner = if (checkmark1.visible) match.player1 else null
						notifyItemChanged(matches.indexOf(match))
					}
					player2Layout.setOnClickListener {
						checkmark1.visibility = View.GONE
						checkmark2.visible = !checkmark2.visible
						match.winner = if (checkmark2.visible) match.player2 else null
						notifyItemChanged(matches.indexOf(match))
					}
				}
			}
		}
	}
}