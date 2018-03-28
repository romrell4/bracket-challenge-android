package com.romrell4.bracketchallenge.model

/**
 * Created by romrell4 on 3/25/18
 */
data class Bracket(
        var bracketId: Int,
        var userId: Int,
        var tournamentId: Int,
        var name: String,
        var score: Int,
        @JvmSuppressWildcards var rounds: List<List<Match>>?
)