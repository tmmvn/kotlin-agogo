package com.venemies.pipes

import com.venemies.GlobalData

/** Creates a download link to the input file with a download icon attached.*/ // TODO: Update icon font to svg img
class DownloadLink:Pipe()
{
	override fun pipe(i:String)
	{
		if(!inConditional)
		{
			output += "<a href='${params["dl_dir"]}/${params["link"]}' "
			output += "aria-label='${i}'. Opens a pdf file download.'>"
			output += i
			output += "<sup><i aria-hidden='true' class='icon-inline'>"
			output += "&#xf019"
			output += "</i></sup></a>"
		}
	}

	override fun prime(i:String?)
	{
		params.putAll(GlobalData.getAll())
		if(i == null)
		{
			println("No link passed to DownloadLink")
			return
		}
		params["link"] = i
	}
}
