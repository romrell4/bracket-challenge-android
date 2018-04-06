package com.romrell4.bracketchallenge.model

import java.io.Serializable

/**
 * Created by romrell4 on 3/25/18
 */
data class Match(
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
): Serializable {
	var player1: Player?
		get() = Player(player1Id, player1Name, seed1)
		set(value) {
			player1Id = value?.playerId
			player1Name = value?.name
			seed1 = value?.seed
		}
	var player2: Player?
		get() = Player(player2Id, player2Name, seed1)
		set(value) {
			player1Id = value?.playerId
			player1Name = value?.name
			seed1 = value?.seed
		}
	var winner: Player?
		get() = Player(winnerId, winnerName, null)
		set(value) {
			winnerId = value?.playerId
			winnerName = value?.name
		}

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