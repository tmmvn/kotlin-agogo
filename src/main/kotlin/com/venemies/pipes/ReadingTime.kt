package com.venemies.pipes

import kotlin.math.ceil

/** Provides a time label for reading time based on word count of content
 * piped in. Outputs the time to #readingtime. */
class ReadingTime:Pipe()
{
	override fun close():String
	{
		if(!inConditional)
		{
			val content = params.get("content")
			if(content == null)
			{
				print("Trying to calculate reading time without content.")
				return ""
			}
			val words = content.split(" ").count()
			val englishReadingSpeed = 238.0
			val englishSpeakingSpeed = 183.0
			val readingTime = ceil(words / englishReadingSpeed).toInt()
			val listeningTime = ceil(words / englishSpeakingSpeed).toInt()
			val outputString =
				"<time aria-label='$listeningTime minute listening time' " + "datetime='PT${readingTime}M'>" + "$readingTime minute read" + "</time>"
			params["readingtime"] = outputString
			output += content
		}
		return super.close()
	}

	override fun prime(i:String?)
	{
		params["content"] = ""
	}

	override fun pipe(i:String)
	{
		if(!inConditional)
		{
			params["content"] += i + "\n"
		}
	}

	override fun resume(i:String)
	{
		if(!inConditional)
		{
			params["content"] += i + "\n"
		}
	}
}
