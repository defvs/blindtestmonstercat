package dev.defvs.blindtestmonstercat

import io.kvision.state.ObservableValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object Model {
	private val gameService = GameService()
	
	val username: String? = null
	
	var game: Game? = null
	var currentRound: Round? = null
	
	val outboundMessages = Channel<ClientGameMessage>()
	
	suspend fun createGame(settings: GameSettings): Unit =
		username?.let { gameService.createGame(it, settings) } ?: throw UserNotSetException()
	
	suspend fun connect() {
		AppScope.launch {
			while (true) {
				gameService.socketConnection { output, input ->
					coroutineScope {
						launch {
							for (message in outboundMessages) output.send(message)
						}
						launch {
							for (message in input) handleInboundMessage(message)
						}
					}
				}
				console.warn("Websocket disconnected. Reconnecting in 2 seconds.")
				delay(2000)
			}
		}
	}
	
	private fun handleInboundMessage(message: ServerGameMessage) {
		when (message.messageType) {
			ServerGameMessageType.Game -> game = Json.decodeFromString(message.data!!)
			ServerGameMessageType.Round -> currentRound = Json.decodeFromString(message.data!!)
			ServerGameMessageType.RoundGuessStart -> TODO()
			ServerGameMessageType.RoundGuessEnd -> TODO()
			ServerGameMessageType.TrackAnswers -> TODO("Update answers if game admin")
			ServerGameMessageType.TrackScores -> TODO()
			ServerGameMessageType.Error -> {} // unused for now
			ServerGameMessageType.OK -> {} // unused for now
		}
	}
	
	suspend fun sendMessage(messageType: ClientGameMessageType, data: String?) =
		username?.let { outboundMessages.send(ClientGameMessage(it, messageType, data)) } ?: throw UserNotSetException()
	
	suspend fun getGame() = sendMessage(ClientGameMessageType.GetGame, null)
	suspend fun getRound() = sendMessage(ClientGameMessageType.GetRound, null)
	suspend fun sendAnswer(answer: String) = sendMessage(ClientGameMessageType.SendAnswer, answer)
	suspend fun sendChosenTrack(trackID: String) = sendMessage(ClientGameMessageType.SendChosenTrack, trackID)
	suspend fun sendScores(scores: Map<String /* username */, Int /* score */>) =
		sendMessage(ClientGameMessageType.SendScores, scores.map { "${it.key}=${it.value}" }.joinToString("\n"))
	suspend fun joinGame(owner: String) = sendMessage(ClientGameMessageType.JoinGame, owner)
	suspend fun leaveGame() = sendMessage(ClientGameMessageType.LeaveGame, null)
}
