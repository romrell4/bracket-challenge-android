package com.romrell4.bracketchallenge.model

/**
 * Created by romrell4 on 3/25/18
 */
data class Bracket(
		var bracketId: Int? = null,
		var userId: Int? = null,
		var tournamentId: Int? = null,
		var name: String,
		var score: Int = 0,
		@JvmSuppressWildcards var rounds: List<List<Match>>? = null
)