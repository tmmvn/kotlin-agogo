package com.venemies

import com.venemies.pipes.Pipe
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.Properties

object AvailablePipes
{
	private val pipes:MutableList<Pair<String, Pipe>> = ArrayList()

	init
	{
		if(File("Pipes.properties").exists())
		{
			val properties = Properties()
			val stream = File("Pipes.properties").inputStream()
			try
			{
				properties.load(stream)
			}
			catch(e:NullPointerException)
			{
				println("Error loading pipes: $e")
			}
			catch(e:IOException)
			{
				println("Error loading pipes: $e")
			}
			catch(e:IllegalArgumentException)
			{
				println("Error pipes: $e")
			}
			finally
			{
				stream.close()
			}
			for(pipe in properties.stringPropertyNames())
			{
				println("Adding pipe $pipe")
				try
				{
					val className = properties.getProperty(pipe)
					if(className != null)
					{
						println("Matching pipe class $className")
						pipes.add(
							Pair(
								pipe, Class.forName(className)
									.getDeclaredConstructor()
									.newInstance() as Pipe
							)
						)
					}
					else
					{
						println("Tried to load $pipe that does not exist!")
					}
				}
				catch(error:ClassNotFoundException)
				{
					println("Tried to load pipe $pipe that does not exist!")
				}
				catch(e:LinkageError)
				{
					println("Loading pipe had an error $e")
				}
				catch(e:ExceptionInInitializerError)
				{
					println("Loading pipe had an error $e")
				}
				catch(e:NoSuchMethodException)
				{
					println("Loading pipe had an error $e")
				}
				catch(e:SecurityException)
				{
					println("Loading pipe had an error $e")
				}
				catch(e:IllegalAccessException)
				{
					println("Loading pipe had an error $e")
				}
				catch(e:IllegalArgumentException)
				{
					println("Loading pipe had an error $e")
				}
				catch(e:InstantiationException)
				{
					println("Loading pipe had an error $e")
				}
				catch(e:InvocationTargetException)
				{
					println("Loading pipe had an error $e")
				}
			}
		}
		else
		{
			println("No pipes properties file found.")
		}
	}

	fun get(key:String):Pipe?
	{
		println("Loading pipe for $key")
		return pipes.find {it.first == key}?.second
	}
}
