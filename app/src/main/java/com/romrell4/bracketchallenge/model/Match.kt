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
        var player2Id: Int?,
        var seed1: Int?,
        var seed2: Int?,
        var winnerId: Int?
)