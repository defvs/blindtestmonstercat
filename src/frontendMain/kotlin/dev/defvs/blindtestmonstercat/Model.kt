package dev.defvs.blindtestmonstercat

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Model {
	private val gameService = GameService()
	
	val username: String? = null
	
	val game: Game? = null
	
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
	
	suspend fun handleInboundMessage(message: ServerGameMessage) {
	
	}
	
	suspend fun getGame(owner: String) =
		username?.let { outboundMessages.send(ClientGameMessage(it, ClientGameMessageType.GetGame, owner)) }
			?: throw UserNotSetException()
}
