package com.romrell4.bracketchallenge.model

import java.util.*

/**
 * Created by romrell4 on 2/25/18
 */
data class Tournament(
        var tournamentId: Int,
        var name: String?,
        var masterBracketId: Int?,
        var drawsUrl: String?,
        var imageUrl: String?,
        var startDate: Date?,
        var endDate: Date?
)