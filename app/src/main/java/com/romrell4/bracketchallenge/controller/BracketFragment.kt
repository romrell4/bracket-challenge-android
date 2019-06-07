package com.romrell4.bracketchallenge.controller

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.*
import com.romrell4.bracketchallenge.support.showToast
import com.romrell4.bracketchallenge.support.visible
import kotlinx.android.synthetic.main.fragment_bracket.*
import kotlinx.android.synthetic.main.row_match.view.*
import kotlinx.android.synthetic.main.view_bracket.*
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

			//Whenever this is set, reset changes made
			changesMade = false

			setupViewBracketUI()
		}
	protected var masterBracket: Bracket? = null
		set(value) {
			field = value
			setupViewBracketUI()
		}
	private var changesMade = false

	//Overridable values
	protected abstract fun areCellsClickable(): Boolean

	protected abstract val cellNotClickableReason: String
	protected open fun getTextColor(playerId: Int?, predictionId: Int?, winnerId: Int?) = ContextCompat.getColor(requireActivity(), R.color.black)

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
		if (areCellsClickable()) {
			inflater?.inflate(R.menu.save_tournament, menu)
		}
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_bracket, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		tournament = arguments?.getParcelable(TOURNAMENT_EXTRA) ?: throw IllegalStateException()
		tournament.masterBracketId?.let {
			Client.createApi().getBracket(tournament.tournamentId, it).enqueue(object: Client.SimpleCallback<Bracket>(requireActivity()) {
				override fun onResponse(data: Bracket?, errorResponse: Response<Bracket>?) {
					data?.let {
						masterBracket = it
					}
				}
			})
		}
	}

	override fun onStop() {
		super.onStop()

		saveBracket()
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
		R.id.saveBracket -> {
			saveBracket(true)
			true
		}
		else -> false
	}

	//Custom Functions

	private fun setupViewBracketUI() {
		if (bracket != null && masterBracket != null) {
			viewSwitcher.displayedChild = VIEW_BRACKET_INDEX
			matchesViewSwitcher.displayedChild = VIEW_BRACKET_ROUNDS_INDEX

			scoreTextView.text = getString(R.string.score_format, bracket?.score)

			//If the adapter already exists, just update the viewPager (they just tapped save)
			if (viewPager.adapter != null) {
				//This will transitively notify each recycler view within the adapter
				viewPager.adapter?.notifyDataSetChanged()
			} else {
				//Tell the view pager to keep all pages in memory (so that we can scroll between them)
				bracket?.rounds?.let { viewPager.offscreenPageLimit = it.size - 1 }
				viewPager.adapter = RoundPagerAdapter()
			}
		}
	}

	private fun saveBracket(showToast: Boolean = false) {
		//Only save if they've actually made changes
		if (changesMade) {
			bracket?.let { it ->
				it.tournamentId?.let { tournamentId ->
					it.bracketId?.let { bracketId ->
						Client.createApi().updateBracket(tournamentId, bracketId, it).enqueue(object: Client.SimpleCallback<Bracket>(requireActivity()) {
							override fun onResponse(data: Bracket?, errorResponse: Response<Bracket>?) {
								data?.let {
									changesMade = false
									bracket = it

									if (showToast) {
										showToast(R.string.bracket_update_success)
									}
								}
							}
						})
					}
				}
			}
		} else {
			if (showToast) {
				showToast(R.string.no_changes)
			}
		}
	}

	inner class RoundPagerAdapter: PagerAdapter() {
		private var recyclerViews = arrayOfNulls<RecyclerView>(count)
		private var scrollListeners = arrayOfNulls<RecyclerView.OnScrollListener>(count)

		override fun getCount() = bracket?.rounds?.size ?: 0
		override fun isViewFromObject(view: View, `object`: Any) = view == `object`
		override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as? View)

		override fun instantiateItem(container: ViewGroup, position: Int): Any {
			val roundView = layoutInflater.inflate(R.layout.view_round, container, false)
			(roundView as? RecyclerView)?.apply {
				layoutManager = LinearLayoutManager(activity)
				adapter = MatchAdapter(position)

				recyclerViews[position] = this
				scrollListeners[position] = object: RecyclerView.OnScrollListener() {
					override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
						super.onScrolled(recyclerView, dx, dy)
						recyclerViews.zip(scrollListeners)
								.filter { it.first != recyclerView }
								.map { pair -> pair.second?.let { pair.first?.removeOnScrollListener(it) }; pair }
								.map { it.first?.scrollBy(dx, dy); it }
								.forEach { pair -> pair.second?.let { pair.first?.addOnScrollListener(it) } }
					}
				}.also { addOnScrollListener(it) }
			}
			container.addView(roundView)
			return roundView
		}

		override fun notifyDataSetChanged() {
			super.notifyDataSetChanged()
			recyclerViews.forEach { it?.adapter?.notifyDataSetChanged() }
		}

		inner class MatchAdapter(private val round: Int): RecyclerView.Adapter<MatchAdapter.ViewHolder>() {
			private val matches: List<Match>
				get() = bracket?.rounds?.get(round) ?: emptyList()
			private val masterMatches: List<Match>
				get() = masterBracket?.rounds?.get(round) ?: emptyList()

			override fun getItemCount() = matches.size
			override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(layoutInflater.inflate(R.layout.row_match, parent, false))
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
						val cardHeight = resources.getDimension(R.dimen.match_card_height)
						val cardMargin = resources.getDimension(R.dimen.match_card_margin)
						val newMargin = ((cardHeight / 2 + cardMargin) * (2.0.pow(match.round - 1) - 1) + cardMargin).toInt()
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
					val matchTapped = { topTapped: Boolean ->
						if (areCellsClickable()) {
							val selectedPlayer = if (topTapped) match.player1 else match.player2

							//Only change if the click a non-blank cell
							if (selectedPlayer != null) {
								val selectedCheckmark = if (topTapped) checkmark1 else checkmark2
								val otherCheckmark = if (topTapped) checkmark2 else checkmark1
								selectedCheckmark.visible = !selectedCheckmark.visible
								otherCheckmark.visible = false

								val winner = if (selectedCheckmark.visible) selectedPlayer else null
								updateWinnerAndNextRound(match, winner)
								bind(match, masterMatch)
							}
						} else {
							showToast(cellNotClickableReason, Toast.LENGTH_SHORT)
						}
					}

					player1Layout.setOnClickListener {
						matchTapped(true)
					}
					player2Layout.setOnClickListener {
						matchTapped(false)
					}
				}

				private fun updateWinnerAndNextRound(match: Match, winner: Player?) {
					changesMade = true
					match.winner = winner
					recyclerViews.getOrNull(match.round)?.also {
						val currentPositionIndex = match.position - 1 //Position is 1 indexed
						val nextPositionIndex = currentPositionIndex / 2 //This will always round down
						val nextMatch = bracket?.rounds?.get(match.round)?.get(nextPositionIndex) //Round is 1 indexed, so this will load the next round's match
						if (currentPositionIndex % 2 == 0) {
							nextMatch?.player1 = winner
						} else {
							nextMatch?.player2 = winner
						}
						it.adapter?.notifyItemChanged(nextPositionIndex)
					}
				}
			}
		}
	}
}