package dev.defvs.blindtestmonstercat

import io.kvision.annotations.KVService
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.Serializable



@KVService
interface IGameService {
	/**
	 * @param owner the game creator username
	 * @param settings [GameSettings] for the game.
	 * @return an [Int] corresponding to the ID of the game, or `null` if failed to create.
	 */
	suspend fun createGame(owner: String, settings: GameSettings): Int?
	
	/**
	 * @param gameID the ID of the game to poll
	 * @return [GameSettings] for the game or `null` if failed to get.
	 */
	suspend fun getGameSettings(gameID: Int): GameSettings?
	
	/**
	 * @param gameID the ID of the game to poll
	 * @return result of the set.
	 */
	suspend fun setGameSettings(gameID: Int, settings: GameSettings): Boolean
	
	// input = going to server. output = going to client.
	suspend fun socketConnection(input: ReceiveChannel<ClientGameMessage>, output: SendChannel<ServerGameMessage>) {}
}
