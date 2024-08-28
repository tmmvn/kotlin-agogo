package com.venemies.pipes

import com.venemies.GlobalData

/** Copyright 2023, Tommi Venemies
 * Licensed under the BSD-4-Clause.
 * Base pipe that all pipes should mixin and override as needed.
 * As default, has single output, a hash for params, and state to see when
 * blocked.
 *
 * When pipe is encountered in a .she-file (plan), prime is called with
 * general site data (parsed by siteparser from .ini) and whatever input from
 * the pipename row as params. The default implementation just passes the
 * input to the output. The input is empty by default.
 *
 * When an |end is encountered in a .she-file (plan), close is called on the
 * last activated pipe. The default replaces all params hash keys
 * (#param_name) in the output with the value of the hash. Finally, the
 * output is returned. The next pipe in the stack then gets a resume call
 * with whatever the close returned.
 *
 * When a ^ is encountered in a .she-file (plan), the pass_param is called on
 * the last activated pipe. First param is parameter name, second is anything
 * after the param name in the .she-file. Default implementation saves these
 * to params for replacing in close.
 *
 * Note that the default implementation adds # on its own.
 *
 * When a % is encountered in a .she-file (plan), the block method is called
 * on the last activated pipe. By default, this checks the "if" params hash
 * has the passed in key. If not, anything send to the pipe method will be
 * ignored until %end. Otherwise, nothing happens.
 *
 * When a %end is encountered in a .she-file (plan), the unblock is called
 * on the last activated pipe. By default, this resets the pipe ignore mode.
 *
 * Any row not starting with |, ^, or % will get sent to the pipe method of
 * the last activated pipe. Default implementation adds this to the output.*/
open class Pipe
{
	protected var output:String = ""
	protected var params:MutableMap<String, String> = mutableMapOf()
	protected var inConditional:Boolean = false
	open fun block(c:String)
	{
		if(!params.containsKey(c) || params[c] != null)
		{
			inConditional = true
		}
	}

	open fun close():String
	{
		params.forEach {(key, value) ->
			output = output.replace("#$key", value)
		}
		return output
	}

	open fun prime(i:String?)
	{
		params.putAll(GlobalData.getAll())
		if(!inConditional)
		{
			if(i != null)
			{
				output += i
				output += "\n"
			}
		}
	}

	open fun passParam(pr:String, v:String)
	{
		if(!inConditional)
		{
			params[pr] = v
		}
	}

	open fun pipe(i:String)
	{
		if(!inConditional)
		{
			output += i
			output += "\n"
		}
	}

	open fun resume(i:String)
	{
		if(!inConditional)
		{
			output += i
			output += "\n"
		}
	}

	open fun unblock()
	{
		inConditional = false
	}
}
