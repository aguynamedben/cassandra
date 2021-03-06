/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.cli;

import org.apache.commons.cli.*;

/**
 *
 * Used to process, and act upon the arguments passed to the CLI.
 *
 */
public class CliOptions {

    private static Options options = null; // Info about command line options

    // Command line options
    private static final String HOST_OPTION = "host";
    private static final String PORT_OPTION = "port";
    private static final String UNFRAME_OPTION = "unframed";
    private static final String DEBUG_OPTION = "debug";
    private static final String USERNAME_OPTION = "username";
    private static final String PASSWORD_OPTION = "password";
    private static final String KEYSPACE_OPTION = "keyspace";
    private static final String BATCH_OPTION = "batch";
    private static final String HELP_OPTION = "help";
    private static final String FILE_OPTION = "file";
    
    // Default values for optional command line arguments
    private static final int    DEFAULT_THRIFT_PORT = 9160;

    // Register the command line options and their properties (such as
    // whether they take an extra argument, etc.
    static
    {
        options = new Options();
        options.addOption(HOST_OPTION, true, "cassandra server's host name");
        options.addOption(PORT_OPTION, true, "cassandra server's thrift port");  
        options.addOption(UNFRAME_OPTION, false, "cassandra server's framed transport");
        options.addOption(DEBUG_OPTION, false, "display stack traces");  
        options.addOption(USERNAME_OPTION, true, "username for cassandra authentication");
        options.addOption(PASSWORD_OPTION, true, "password for cassandra authentication");
        options.addOption(KEYSPACE_OPTION, true, "cassandra keyspace user is authenticated against");
        options.addOption(BATCH_OPTION, false, "enabled batch mode (supress output; errors are fatal)");
        options.addOption(FILE_OPTION, true, "load statements from the specific file.");
        options.addOption(HELP_OPTION, false, "usage help.");
    }

    private static void printUsage()
    {
        System.err.println("Usage: cassandra-cli --host hostname [--port <portname>] [--file <filename>] [--unframed] [--debug]");
        System.err.println("\t[--username username] [--password password] [--keyspace keyspace] [--batch] [--help]");
    }

    public void processArgs(CliSessionState css, String[] args)
    {
        CommandLineParser parser = new PosixParser();
        try
        {
            CommandLine cmd = parser.parse(options, args);
            
            if (!cmd.hasOption(HOST_OPTION))
            {
                // host name not specified in command line.
                // In this case, we don't implicitly connect at CLI startup. In this case,
                // the user must use the "connect" CLI statement to connect.
                //
                css.hostName = null;
                
                // HelpFormatter formatter = new HelpFormatter();
                // formatter.printHelp("java com.facebook.infrastructure.cli.CliMain ", options);
                // System.exit(1);
            }
            else 
            {
                css.hostName = cmd.getOptionValue(HOST_OPTION);
            }

            // Look to see if frame has been specified
            if (cmd.hasOption(UNFRAME_OPTION))
            {
                css.framed = false;
            }

            // Look to see if frame has been specified
            if (cmd.hasOption(DEBUG_OPTION))
            {
                css.debug = true;
            }

            // Look for optional args.
            if (cmd.hasOption(PORT_OPTION))
            {
                css.thriftPort = Integer.parseInt(cmd.getOptionValue(PORT_OPTION));
            }
            else
            {
                css.thriftPort = DEFAULT_THRIFT_PORT;
            }
         
            // Look for authentication credentials (username and password)
            if (cmd.hasOption(USERNAME_OPTION)) 
            {
            	css.username = cmd.getOptionValue(USERNAME_OPTION);
            }
            if (cmd.hasOption(PASSWORD_OPTION))
            {
            	css.password = cmd.getOptionValue(PASSWORD_OPTION);
            }
            
            // Look for keyspace
            if (cmd.hasOption(KEYSPACE_OPTION)) 
            {
            	css.keyspace = cmd.getOptionValue(KEYSPACE_OPTION);
            }
            
            if (cmd.hasOption(BATCH_OPTION))
            {
                css.batch = true;
            }

            if (cmd.hasOption(FILE_OPTION))
            {
                css.filename = cmd.getOptionValue(FILE_OPTION);
            }

            if (cmd.hasOption(HELP_OPTION))
            {
                printUsage();
                System.exit(1);
            }
        }
        catch (ParseException e)
        {
            printUsage();
            System.err.println("\n" + e.getMessage());
            System.exit(1);
        }
    }
}
