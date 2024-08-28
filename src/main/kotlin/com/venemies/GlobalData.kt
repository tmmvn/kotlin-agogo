package com.venemies

import java.io.File
import java.io.IOException
import java.util.Properties

object GlobalData
{
	private val properties = Properties()

	init
	{
		if(File("Global.properties").exists())
		{
			try {
				properties.load(File("Global.properties").inputStream())
			}
			catch(e: NullPointerException) {
				println("Error loading global properties: $e")
			}
			catch(e:IOException) {
				println("Error loading global properties: $e")
			}
			catch(e: IllegalArgumentException) {
				println("Error loading global properties: $e")
			}
		}
		else
		{
			println("No global properties file found.")
		}
	}

	fun get(key:String):String? = properties.getProperty(key)
	fun getAll():List<Pair<String, String>>
	{
		val returnValue:MutableList<Pair<String, String>> = mutableListOf()
		for(key in properties.stringPropertyNames())
		{ // Get the value for the key
			val value:String = properties.getProperty(
				key
			)                // Print the key and value
			returnValue.add(key to value)
		}
		return returnValue
	}
}
