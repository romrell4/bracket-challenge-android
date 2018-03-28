package com.romrell4.bracketchallenge.model

/**
 * Created by romrell4 on 3/25/18
 */
open class Match(
        var matchId: Int,
        var bracketId: Int,
        var round: Int,
        var position: Int,
        var player1Id: Int?,
        var player1Name: String?,
        var player2Id: Int?,
        var player2Name: String?,
        var seed1: Int?,
        var seed2: Int?,
        var winnerId: Int?,
        var winnerName: String?
) {
    fun player1Full() =
            seed1?.let {
                "$player1Name ($it)"
            } ?: player1Name?.let {
                it
            } ?: run {
                ""
            }

    fun player2Full() =
            seed2?.let {
                "$player2Name ($it)"
            } ?: player2Name?.let {
                it
            } ?: run {
                ""
            }
}