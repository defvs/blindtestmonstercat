package dev.defvs.blindtestmonstercat

import kotlinx.serialization.Serializable

data class Game(
	val settings: GameSettings,
	val rounds: ArrayList<GameRound>,
	val players: ArrayList<String>,
	val activePlayers: ArrayList<String>,
) {
	fun getAllTotalScores() = players.associateWith { getTotalScore(it) }
	fun getTotalScore(player: String) = rounds.sumOf { round -> round.getTotalScore(player) }
}

/**
 * @param maxPlayers Maximum amount of players in the game
 * @param roundsPerPlayer
 * @param tracksPerRound Number of tracks per round
 * @param guessDuration in seconds
 */
@Serializable
data class GameSettings(
	val maxPlayers: Int,
	val roundsPerPlayer: Int,
	val tracksPerRound: Int,
	val guessDuration: Int,
)

data class GameRound(
	val roundAdmin: String,
) {
	val tracks = ArrayList<String>()
	val scores = ArrayList<HashMap<String, Int>>()
	
	fun getTotalScore(player: String) = scores.sumOf { track ->
		track.filterKeys { key -> key == player }.values.sum()
	}
}