package com.venemies

import com.venemies.pipes.Pipe
import java.io.File
import kotlin.reflect.full.createInstance

/** Parses .she (Shedio) files. These act as plans for the pipe system.*/
class Processor
{
	companion object
	{
		const val CONDITIONAL_KEY = "<>"
		const val INCLUDE_KEY = ">|"
		const val PARAM_KEY = "^"
		const val TAG_KEY = "|"
		val IGNORED_FILES = listOf(".", "_")
	}

	private val target = "./${GlobalData.get("target")}"
	private val source = "./${GlobalData.get("source")}"
	private val activePipes = mutableListOf<Pipe>()
	private var activeOutput = ""

	init
	{
		createOutputDirectories()
	}

	private fun checkAndCreateDir(d:String)
	{
		if(!File(d).exists())
		{
			File(d).mkdirs()
		}
	}

	private fun createOutputDirectories()
	{
		checkAndCreateDir(target)
	}
	/** The start point of parsing. Triggers a directory parse of the source
	 * directory.*/
	fun parse()
	{
		println("Parsing $source")
		try
		{
			File(source).walkTopDown().onEnter {
				!IGNORED_FILES.stream().anyMatch {i -> it.name.startsWith(i)}
			}.forEach {file ->
					if(file.isHidden)
					{
						println("Ignoring hidden file ${file.name}")
					}
					else
					{
						if(file.isFile)
						{
							println("Processing ${file.name}")
							parseFile(file)
						}
					}
				}
		}
		catch(e:SecurityException)
		{
			println("Error when reading directory: $e")
		}
	}
	/** Parses an env conditional row. This is indicated by a % in the .she
	 * files. When encountering a %varname, if the variable exists in the
	 * env.ini, the current active pipe receives a block signal. When
	 * encountering a %end, the current active pipe receives an unblock
	 * signal. When encountering a %varname foo, the block happens only if
	 * the env.ini file has varname=foo in it. The varname foo combo is
	 * provided as a parameter to the pipe for a chance to implement custom
	 * logic.*/
	private fun parseConditional(r:String)
	{
		println("Parsing conditional $r")
		val parsed = r.split(CONDITIONAL_KEY, limit = 2)
		val parts = parsed.last().split(" ", limit = 2)
		val param = parts.first()
		if(param == "end")
		{
			activePipes.last().unblock()
		}
		else
		{
			val envDataValue = EnvData.get(param)
			if(parts.size == 1)
			{
				if(envDataValue == null)
				{
					activePipes.last().block(param)
				}
			}
			else
			{
				if(envDataValue != null)
				{
					val value = parts[1].trim()
					if(envDataValue != value)
					{
						activePipes.last().block(parsed.last())
					}
				}
				else
				{                    // TODO: This needs to pass the parts
					activePipes.last().block(param)
				}
			}
		}
	}
	/** Parses a shedio (.she) file from source to target. These files are
	 * pipe shematics that instruct on the processing of the content.
	 * Triggers parse_pipe for logic.*/
	private fun parseContent(s:File)
	{
		parsePipe(s)
		if(activePipes.isNotEmpty())
		{
			println("Error. More than one pipe active when finishing.")
			println(
				"You probably forgot to |end a pipe."
			)            // TODO Make actual error or remove if not actually fatal
		}
	}
	/** Parses the given file. If the file is a shedio file (.she), it gets
	 * processed with parse_content. Otherwise, the file is statically copied
	 * to target, unless @extensions contains a class to handle the
	 * processing. See parse_extension for details.*/
	private fun parseFile(s:File)
	{
		println("Processing $s")
		activeOutput = ""
		var target = s.parentFile.absolutePath.replace(source, target)
		target += "/"
		target += s.name
		when(s.extension)
		{
			"she" ->
			{
				parseContent(s)
				writeFile(s, target)
			}

			else -> staticCopy(s, target)
		}
	}
	/** Parses a parameter row. This is indicated with a ^ in the .she files.
	 *  For example ^paramname foo. The currently active pipe is passed the
	 *  paramaname and then foo.*/
	private fun parseParam(s:String)
	{
		val paramParts = s.split(" ", limit = 2)
		activePipes.last().passParam(
			paramParts.first().removePrefix(PARAM_KEY), paramParts.last()
		)
	}
	/** Processes passed file line by line passing each row to parse_row*/
	private fun parsePipe(s:File)
	{
		s.forEachLine {
			parseRow(it)
		}
	}
	/** Parses the passed row for .she file logic. | triggers parse_tag, ^
	 * triggers parse_param, % triggers parse_conditional, ~ triggers
	 * parse_include, otherwise row is piped to the currently actived pipe.*/
	private fun parseRow(s:String)
	{
		val row = s.trim()
		when
		{
			row.startsWith(TAG_KEY) -> parseTag(row)
			row.startsWith(
				PARAM_KEY
			) -> if(activePipes.isNotEmpty()) parseParam(row)

			row.startsWith(
				CONDITIONAL_KEY
			) -> if(activePipes.isNotEmpty()) parseConditional(row)

			row.startsWith(INCLUDE_KEY) -> if(activePipes.isNotEmpty())
			{
				parseInclude(
					row
				)                //@activePipes.last.pipe include_content
			}

			else -> if(activePipes.isNotEmpty()) activePipes.last().pipe(row)
		}
	}
	/** Parses a row that had |. Example |pipename foo. Tries to find a pipe
	 * called pipename and opens it passing in global data and foo. Adds that
	 * pipe as the last pipe in the stack. If encounters |end, calls tag_end 	*/
	private fun parseTag(s:String)
	{
		val parts = s.split(TAG_KEY, limit = 2)
		val tagParts = parts.last().split(" ").toMutableList()
		val tag = tagParts.first().trim()
		if(tag == "end")
		{
			tagEnd()
		}
		else
		{
			val pipeClass = AvailablePipes.get(tag)
			if(pipeClass != null)
			{
				val pipe = pipeClass::class.createInstance()
				activePipes.add(pipe)
				tagParts.removeFirst()
				if(tagParts.isNotEmpty()) pipe.prime(tagParts.joinToString(" "))
				else pipe.prime(null)
			}
			else
			{
				println("Pipe $tag not found")
			}
		}
	}

	private fun parseInclude(r:String)
	{
		val parts = r.split(INCLUDE_KEY, limit = 2)
		val filename = "${GlobalData.get("includes")}/${parts.last()}.she"
		println("Loading include: $filename")
		if(File(filename).exists())
		{
			parsePipe(File(filename))
		}
	}
	/** Does a static copy of source to target*/
	private fun staticCopy(s:File, t:String)
	{
		checkAndCreateDir(File(t).parentFile.absolutePath)
		s.inputStream().use {input ->
			File(t).outputStream().use {output ->
				input.copyTo(output)
			}
		}
	}
	/** Closes current pipe and passes the output to the previous pipe in
	stack. If no more pipes in stack, passes to @activeOutput.*/
	private fun tagEnd()
	{
		val activePipe = activePipes.removeLast()
		if(activePipes.isNotEmpty())
		{
			activePipes.last().resume(activePipe.close())
		}
		else
		{
			activeOutput += activePipe.close()
		}
	}

	private fun writeFile(s:File, t:String)
	{
		var target = File(t).parentFile.absolutePath
		checkAndCreateDir(target)
		target += "/"
		target += File(
			t
		).nameWithoutExtension        // TODO: Figure out how to specify extension to pipe to.
		target += ".html"
		println("Saving parsed $s, To: $t")
		File(target).writeText(activeOutput)
	}
}
