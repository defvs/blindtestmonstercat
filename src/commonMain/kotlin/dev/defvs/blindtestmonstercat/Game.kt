package dev.defvs.blindtestmonstercat

import kotlinx.serialization.Serializable


/**
 * An instance of a game. Contains settings, list of rounds, and list of players in the game.
 */
@Serializable
data class Game(
	val settings: GameSettings,
	val rounds: ArrayList<Round>,
	val players: ArrayList<Player>,
) {
	fun getAllTotalScores() = players.associateWith { getTotalScore(it) }
	fun getTotalScore(player: Player) = rounds.sumOf { round -> round.answers[player]?.score ?: 0 }
	
	val currentRoundAdmin get() = rounds.lastOrNull()?.roundAdmin
	val nextRoundAdmin get() = players.getOrNull(players.indexOf(currentRoundAdmin))
}

/**
 * Instance of a player in a [Game].
 *
 * @param username Username of the player.
 * @param active Player might have left the game but may join again.
 * 					Active shows if player is currently connected to the game.
 */
@Serializable
data class Player(
	val username: String,
	val active: Boolean,
)

/**
 * Settings of a [Game]
 *
 * @param maxPlayers Maximum amount of players in the game
 * @param roundsPerPlayer Number of rounds per player before the game is over.
 * @param guessDuration In seconds, duration for guesses. Will allow for +2 seconds over this duration.
 */
@Serializable
data class GameSettings(
	val maxPlayers: Int,
	val roundsPerPlayer: Int,
	val guessDuration: Int,
)

/**
 * Round of a [Game]
 *
 * A Round consists of the following steps:
 * 		- Every player gets callback of round start ([ServerGameMessageType.Round] is sent, with empty [answers])
 * 		- Player who has the [Player.username] equals to [roundAdmin] gets to chose a track from the Monstercat API. They return a [ClientGameMessageType.SendChosenTrack] with its ID.
 * 		- Server sends a [ServerGameMessageType.RoundGuessStart]. All players send their answers using [ClientGameMessageType.SendAnswer]. Server re-sends these as [ServerGameMessageType.TrackAnswers] to the round admin.
 * 		- Server sends a [ServerGameMessageType.RoundGuessEnd]. Clients should stop accepting answers and dispatching the last [ClientGameMessageType.SendAnswer]
 * 		- Guess time is over, server sends a [ServerGameMessageType.Round] with [answers] filled. Round admin gets to chose scores for each answer, and sends [ClientGameMessageType.SendScores] at each edit.
 * 		- Server dispatches the scores visually with [ServerGameMessageType.TrackScores]
 * 		- Once guess is over, server syncs clients with a [ServerGameMessageType.Game], and restarts the process.
 *
 * 	@param roundAdmin Defines the player who choses the track for other players to guess, and also decides on the scores of each answer.
 * 	@property trackID ID of the track from the Monstercat API
 * 	@property trackStartTime Offset in seconds of where to start the track at.
 * 	@property answers All the [RoundAnswer]s in this round. Contains both the answer as a [String] and the score given (`null` or [0..2])
 */
@Serializable
data class Round(
	val roundAdmin: Player,
) {
	val trackID: String? = null
	val trackStartTime = 0
	val answers = HashMap<Player, RoundAnswer>()
}

/**
 * An answer in a [Round].
 *
 * @param answer Answer as a [String] given.
 * @param score Score as a nullable [Int?], with `null` corresponding to "no score chosen yet", and [0..2] corresponding to the score chosen.
 */
@Serializable
data class RoundAnswer(
	val answer: String,
	val score: Int?,
)