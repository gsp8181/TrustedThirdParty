package com.team2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * @author Geoffrey Prytherch <gsp8181@users.noreply.github.com>
 * @since 2015-03-02
 *
 */
public class OptionsFactory {
	public static Options returnOptions()
	{
		Options options = new Options();
		options.addOption(new Option("gensig","Generates a signature for use in the program"));
		options.addOption(new Option("sign","Signs a document and submits it with the current signature"));
		options.addOption(new Option("getcontracts","Returns all contracts waiting to be signed"));
		options.addOption(new Option("countersign", "Countersigns a document"));
		options.addOption(new Option("getcompleted","Returns the receipt signature of a remote document"));
		//options.addOption(new Option("id","id of the contract to retrieve"));
		
		return options;
	}
	public static Options gensigOptions()
	{
		Options options = new Options();
		options.addOption(new Option("email","Certificate email"));
		return options;
	}
	public static Options signOptions()
	{
		Options options = new Options();
		options.addOption(new Option("destination","Recipient of the message"));
		options.addOption(new Option("file","File to sign"));
		return options;
	}
	//public static Options getcontractsOptions()
	//{
	//	Options options = new Options();
	//	options.addOption(new Option("gensig","Generates a signature for use in the program"));
	//	return options;
	//}
	public static Options countersignOptions()
	{
		Options options = new Options();
		options.addOption(new Option("id","ID of the contract to countersign"));
		return options;
	}
	public static Options getcompletedOptions()
	{
		Options options = new Options();
		options.addOption(new Option("id","id of the contract to retrieve"));
		return options;
	}
	public static Options allOptions()
	{
		Options options = new Options();
		options.addOption(new Option("gensig","Generates a signature for use in the program"));
		options.addOption(new Option("sign","Signs a document and submits it with the current signature"));
		options.addOption(new Option("getcontracts","Returns all contracts waiting to be signed"));
		options.addOption(new Option("countersign", "Countersigns a document"));
		options.addOption(new Option("getcompleted","Returns the receipt signature of a remote document"));
		options.addOption(new Option("email","Certificate email"));
		options.addOption(new Option("destination","Recipient of the message"));
		options.addOption(new Option("file","File to sign"));
		options.addOption(new Option("id","ID of the contract"));
		options.addOption(new Option("id","id of the contract"));
		//options.addOption(new Option("id","id of the contract to retrieve"));
		
		return options;
	}
}
