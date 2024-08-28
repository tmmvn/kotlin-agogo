package com.venemies.pipes

import com.venemies.GlobalData
import java.io.File

/** Provides support for protypo (.prot) templates. Protypo files should be
 * basic HTML files of reusable components. You can use #param_name to
 * replace values in the files dynamically. You can pipe content to the
 * template pipe before closing to replace #content automatically. You can
 * use ~ inside the templates to include another component template.*/
class Template:Pipe()
{
	private var templateDir:String? = ""
	override fun prime(i:String?)
	{
		templateDir = GlobalData.get("templates")
		val filename = "$templateDir/#{i}.prot"
		println("Loading template: $filename")
		if(!File(filename).exists())
		{
			return
		}
		parseTemplate(filename)
		params["content"] = ""
	}

	private fun parseTemplate(s:String)
	{
		File(s).forEachLine {content ->
			val row = content.trim()
			if(row.startsWith("~"))
			{
				val parts = row.split("~", limit = 2)
				val filename = "$templateDir/${parts[1]}.prot"
				println("Loading subtemplate: $filename")
				parseTemplate(filename)
			}
			else if(row.startsWith("^"))
			{
				val paramParts = row.split(
					" ", limit = 2,
				)                // println("Adding param: $row")
				params[paramParts[0].removePrefix("^")] = paramParts[1]
			}
			else
			{
				output += content
			}
		}
		println("...Loaded template.")
	}

	override fun pipe(i:String)
	{
		if(!inConditional)
		{
			params["content"] += i
		}
	}

	override fun resume(i:String)
	{
		if(!inConditional)
		{
			params["content"] += i
		}
	}
}
