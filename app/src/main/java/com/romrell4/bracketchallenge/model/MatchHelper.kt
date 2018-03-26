package com.romrell4.bracketchallenge.model

/**
 * Created by romrell4 on 3/25/18
 */
class MatchHelper(matchId: Int, bracketId: Int, round: Int, position: Int, player1Id: Int?, player2Id: Int?, seed1: Int?, seed2: Int?, winnerId: Int?): Match(matchId, bracketId, round, position, player1Id, player2Id, seed1, seed2, winnerId) {
    var player1: Player? = null
        set(value) {
            field = value
            player1Id = value?.playerId
            seed1 = value?.seed
        }
    var player2: Player? = null
        set(value) {
            field = value
            player2Id = value?.playerId
            seed2 = value?.seed
        }
    var winner: Player? = null
        set(value) {
            field = value
            winnerId = value?.playerId
        }
    var player1Full: String? = null
        get() {
            val name = player1?.name
            if (seed1 != null) {
                return "$name $seed1"
            } else {
                return name
            }
        }
    var player2Full: String? = null
        get() {
            val name = player2?.name
            if (seed2 != null) {
                return "$name $seed2"
            } else {
                return name
            }
        }
}