package com.venemies.pipes

import com.venemies.GlobalData
import java.time.LocalDateTime

/** Provides a dynamic year-based copyright. The copyright automatically goes
 * to a param copyright. So as a bare minimum, pipe in #copyright.
 *
 * Alternatively, you can pipe in a template where the spot for copyright is
 * set. In addition, when piping, you can pass a dynamic copyright. Otherwise,
 * the value for copyright holder is read from global data passed in. The
 * variable read is copyright_holder.*/
class Copyright:Pipe()
{
	override fun prime(i:String?)
	{
		params.putAll(GlobalData.getAll())
		val year = LocalDateTime.now().year
		val copyrightHolder = if(i != null)
		{
			"Copyright (c) $year $i"
		}
		else
		{
			"Copyright (c) $year ${GlobalData.get("copyright_holder")}"
		}
		params["copyright"] = copyrightHolder
	}
}
