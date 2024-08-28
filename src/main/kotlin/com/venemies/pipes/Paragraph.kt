package com.venemies.pipes

import com.venemies.GlobalData

/** Creates a paragraph block. Everything piped in goes to the paragraph.*/
class Paragraph:Pipe()
{
	override fun close():String
	{
		super.close()
		if(!inConditional)
		{
			output += "</p>"
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
		if(!inConditional)
		{
			output += "<p>"
		}
	}
}
