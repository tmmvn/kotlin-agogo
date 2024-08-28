package com.venemies.pipes

import com.venemies.GlobalData

/** Creates a heading. Takes the header level as input and puts everything
 * piped in inside the block.*/
class Heading:Pipe()
{
	override fun close():String
	{
		super.close()
		if(!inConditional)
		{
			output += "</h${params["level"]}>"
		}
		return output
	}

	override fun pipe(i:String)
	{
		if(!inConditional)
		{
			output += i
		}
	}

	override fun prime(i:String?)
	{
		params.putAll(GlobalData.getAll())
		if(i == null)
		{
			println("Did not pass header level.")
			return
		}
		params["level"] = i
		if(!inConditional)
		{
			output += "<h$i>"
		}
	}
}
