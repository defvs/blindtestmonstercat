package dev.defvs.blindtestmonstercat

import io.kvision.Application
import io.kvision.i18n.DefaultI18nManager
import io.kvision.i18n.I18n
import io.kvision.module
import io.kvision.panel.root
import io.kvision.require
import io.kvision.startApplication
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App: Application() {
	
	override fun start(state: Map<String, Any>) {
		I18n.manager =
			DefaultI18nManager(
				mapOf(
					"en" to require("i18n/messages-en.json"),
				)
			)
		
		val root = root("kvapp") {}
	}
}

fun main() {
	startApplication(::App, module.hot)
}
