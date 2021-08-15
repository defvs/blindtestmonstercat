package dev.defvs.blindtestmonstercat

import kotlinx.serialization.Serializable

@Serializable
enum class ClientGameMessageType {
	/**
	 * Request joining a game.
	 * Server should return a [ServerGameMessageType.Game] message.
	 *
	 * Server returns a [ServerGameMessageType.Error] if the requested game is not accessible.
	 *
	 * __Payload__: A [String] containing the owner of the game to join.
	 *
	 */
	JoinGame,
	
	/**
	 * Leave a game.
	 * Server sends an [ServerGameMessageType.OK] message
	 *
	 * __Payload__: Payload ignored. Send `null`.
	 */
	LeaveGame,
	
	/**
	 * Request [Game] object again for re-sync purposes.
	 *
	 * __Payload__: Payload ignored. Send `null`
	 */
	GetGame,
	
	/**
	 * Request current [GameRound] object again for re-sync purposes.
	 *
	 * __Payload__: Payload ignored. Send `null`
	 */
	GetRound,
	
	/**
	 * Send personal answer to a round track.
	 *
	 * Server should send a [ServerGameMessageType.OK] message
	 * Server returns a [ServerGameMessageType.Error] if answer is too late, or client is out of sync.
	 *
	 * __Payload__: A [String] containing the answer.
	 */
	SendAnswer,
	
	/**
	 * Send track chosen by round admin for it to be guessed.
	 *
	 * Server should send a [ServerGameMessageType.OK] message.
	 * Server returns a [ServerGameMessageType.Error] if client is not round admin, or client is out of sync.
	 *
	 * __Payload__: A [String] containing the chosen track.
	 */
	SendChosenTrack,
	
	/**
	 * Send chosen scores by round admin for each answer.
	 *
	 * Should be updated in real-time, everytime round admin changes a score on frontend.
	 *
	 * __Payload__: A [String] containing `key=value` pairs for each player, separated by line feed (`\n`).
	 */
	SendScores,
}

@Serializable
enum class ServerGameMessageType {
	/**
	 * Info about the Game.
	 * Sent on [ClientGameMessageType.JoinGame] or [ClientGameMessageType.GetGame] or at end of round.
	 *
	 * __Payload__: A json-serialized [Game] object
	 */
	Game,
	
	/**
	 * Info about the Round.
	 * Sent at round start before [ClientGameMessageType.SendChosenTrack],
	 * 		after server receives [ClientGameMessageType.SendChosenTrack],
	 * 		2 seconds after server sends [RoundGuessEnd].
	 *
	 * __Payload__: A json-serialized [GameRound] object
	 */
	Round,
	
	/**
	 * When players can start guessing.
	 *
	 * __Payload__: `null`
	 */
	RoundGuessStart,
	
	/**
	 * When players should stop guessing.
	 *
	 * Server should accept a 2 seconds delay after this message was broadcast before denying a [ClientGameMessageType.SendAnswer]
	 *
	 * __Payload__: `null`
	 */
	RoundGuessEnd,
	
	/**
	 * Used to provide real-time feedback of players answers to the round admin.
	 *
	 * __Payload__: A [String] containing `key=value` pairs for each player, separated by line feed (`\n`).
	 */
	TrackAnswers,
	
	/**
	 * Used to provide real-time feedback of round admin scores to the players.
	 *
	 * __Payload__: A [String] containing `key=value` pairs for each player, separated by line feed (`\n`).
	 */
	TrackScores,
	
	/**
	 * Error status message
	 * __Payload__: A [String] with the error in human-readable form.
	 */
	Error,
	
	/**
	 * Acknowledge status message
	 *
	 * __Payload__: A [String] with the int constant of the last message sent.
	 */
	OK,
}

@Serializable
data class ClientGameMessage(
	val sender: String,
	val messageType: ClientGameMessageType,
	val data: String?,
)

@Serializable
data class ServerGameMessage(
	val messageType: ServerGameMessageType,
	val data: String?,
)