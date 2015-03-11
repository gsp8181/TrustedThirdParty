package com.team2;


import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * @author Geoffrey Prytherch <gsp8181@users.noreply.github.com>
 * @since 2015-03-02
 *
 */
public class OptionsFactory {
	public static Options gensigOptions()
	{
		Options options = new Options();
		options.addOption( OptionBuilder.withLongOpt( "email" )
				.isRequired(true)
                .withDescription( "Certificate email" )
                .hasArg()
                .withArgName("email")
                .create("e") );
		return options;
	}
	public static Options signOptions()
	{
		Options options = new Options();
		options.addOption( OptionBuilder.withLongOpt( "destination" )
				.isRequired(true)
                .withDescription( "Recipient of the message" )
                .hasArg()
                .withArgName("email address")
                .create("d") );
		options.addOption( OptionBuilder.withLongOpt( "file" )
				.isRequired(true)
                .withDescription( "File to sign" )
                .hasArg()
                .withArgName("filename")
                .create("f") );
		return options;
	}
	
	
	
	public static Options countersignOptions()
	{
		Options options = new Options();
		options.addOption( OptionBuilder.withLongOpt( "id" )
				.isRequired(true)
                .withDescription( "ID of the contract to countersign" )
                .hasArg()
                .withArgName("Contract ID")
                .create("i") );
		return options;
	}
	
	public static Options getcompletedOptions()
	{
		Options options = new Options();
		options.addOption( OptionBuilder.withLongOpt( "id" )
				.isRequired(true)
                .withDescription( "ID of the contract to retrieve" )
                .hasArg()
                .withArgName("Contract ID")
                .create("i") );
		return options;
	}
	
	public static Options abort()
	{
		Options options = new Options();
		options.addOption( OptionBuilder.withLongOpt( "id" )
				.isRequired(true)
                .withDescription( "ID of the contract to abort" )
                .hasArg()
                .withArgName("Contract ID")
                .create("i") );
		return options;
	}
	
}
