package com.venemies.pipes

import com.venemies.GlobalData

/** Creates a HTML doc. Should be your starting pipe most of the time.*/
class HTMLDoc:Pipe()
{
	override fun close():String
	{
		super.close()
		if(!inConditional)
		{
			output += "</html>"
		}
		return output
	}

	override fun prime(i:String?)
	{
		params.putAll(GlobalData.getAll())
		if(!inConditional)
		{
			output += "<!doctype html>"
			output += "<html lang='#language_code'>"
			output += i
		}
	}
}
