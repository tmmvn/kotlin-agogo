package com.venemies.pipes

import com.venemies.GlobalData

/** Creates a preformatted code block. Takes code language as input and adds
 * everything piped in to the block.*/
class Code:Pipe()
{
	override fun close():String
	{
		super.close()
		if(!inConditional)
		{
			output += "</pre>"
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
		if(i != null)
		{
			params["language"] = i
		}
		if(!inConditional)
		{
			output += "<pre>"
		}
	}
}
