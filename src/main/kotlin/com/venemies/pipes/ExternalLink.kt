package com.venemies.pipes

import com.venemies.GlobalData

/** Creates an external link to the input link. Adds an adjoining icon as an
indicator.*/ // TODO: Update icon font to svg img
class ExternalLink:Pipe()
{
	override fun pipe(i:String)
	{
		if(!inConditional)
		{
			output += "<a href='${params["link"]}' target='_blank' "
			output += "rel='nofollow noreferrer' "
			output += "aria-label='${i} Opens in new tab or window.'>"
			output += i
			output += "<sup><i aria-hidden='true' class='icon-inline'>"
			output += "&#xf08e"
			output += "</i></sup></a>"
		}
	}

	override fun prime(i:String?)
	{
		params.putAll(GlobalData.getAll())
		if(i == null)
		{
			println("No link passed to ExternalLink")
			return
		}
		params["link"] = i
	}
}
