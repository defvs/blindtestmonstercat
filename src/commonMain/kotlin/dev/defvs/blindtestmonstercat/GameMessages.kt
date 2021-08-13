package dev.defvs.blindtestmonstercat

import kotlinx.serialization.Serializable

@Serializable
enum class ClientGameMessageType {
	JoinGame,
	LeaveGame,
	SendAnswer,
	SendChosenTrack,
	SendScore,
	GetScores,
}

@Serializable
enum class ServerGameMessageType {
	GameInfo,
	PlayerList,
	AllScores,
	Answers,
	Score,
	NewRound,
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