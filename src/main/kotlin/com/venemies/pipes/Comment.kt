package com.venemies.pipes

/** Allows bypassing anything piped in*/
class Comment:Pipe()
{
	override fun close():String
	{
		return ""
	}

	override fun pipe(i:String)
	{
		println("Commenting $i")
	}

	override fun prime(i:String?)
	{
	}

	override fun passParam(pr:String, v:String)
	{
	}
}
