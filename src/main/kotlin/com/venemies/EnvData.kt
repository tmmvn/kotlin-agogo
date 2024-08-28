package com.venemies

import java.io.File
import java.io.IOException
import java.util.Properties

object EnvData
{
	private val properties = Properties()

	init
	{
		if(File("Env.properties").exists())
		{
			try
			{
				properties.load(File("Env.properties").inputStream())
			}
			catch(e: NullPointerException) {
				println("Error loading environment properties: $e")
			}
			catch(e:IOException) {
				println("Error loading environment properties: $e")
			}
			catch(e: IllegalArgumentException) {
				println("Error loading environment properties: $e")
			}
		}
		else
		{
			println("No environment properties file found.")
		}
	}

	fun get(key:String):String? = properties.getProperty(key)
}
