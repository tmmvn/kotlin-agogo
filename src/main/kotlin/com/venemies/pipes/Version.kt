package com.venemies.pipes

import com.venemies.GlobalData
import com.venemies.pipes.Pipe
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Provides a dynamic year-based version text. Version automatically goes to
 *  a param named version. So as a bare minimum, pipe in #version.
 *  Alternatively, you can pipe in a template where the spot for version is
 *  set.
 *
 *  In addition, when piping, you can pass a dynamic version. Otherwise, the
 *  value for version is read from global data passed in. The variable read
 *  is version. Version format is YYYY.MM.version.*/
class Version:Pipe()
{
	override fun prime(i:String?)
	{
		val formatter = DateTimeFormatter.ofPattern("yyyy.MM")
		val version = LocalDateTime.now().format(formatter)
		if(i != null)
		{
			params["version"] = "$version.$i"
		}
		else
		{
			params["version"] = "$version.${GlobalData.get("version")}"
		}
	}
}
