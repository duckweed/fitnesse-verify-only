// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.slim;

import java.io.IOException;
import java.util.Arrays;

import util.CommandLine;
import fitnesse.socketservice.SocketService;

public class SlimService extends SocketService {
  public static SlimService instance = null;
  public static boolean verbose;
  public static int port;
  public static boolean verifyOnly = true;

  public static void main(String[] args) throws IOException {
    if (parseCommandLine(args)) {
      startWithFactory(args, new JavaSlimFactory());
    } else {
      parseCommandLineFailed(args);
    }
  }

  protected static void parseCommandLineFailed(String[] args) {
    System.err.println("Invalid command line arguments:" + Arrays.asList(args));
  }

  protected static void startWithFactory(String[] args, SlimFactory slimFactory) throws IOException {
    new SlimService(port, slimFactory.getSlimServer(verbose, verifyOnly));
  }

  protected static boolean parseCommandLine(String[] args) {
    CommandLine commandLine = new CommandLine("[-y] [-v] port");
    if (commandLine.parse(args)) {
      verifyOnly = commandLine.hasOption("y");
      verbose = commandLine.hasOption("v");
      StatementExecutor.setVerifyOnly(verifyOnly);
      MethodExecutor.setVerifyOnly(verifyOnly);
      String portString = commandLine.getArgument("port");
      port = Integer.parseInt(portString);
      return true;
    }
    return false;
  }

  public SlimService(int port, SlimServer slimServer) throws IOException  {
    super(port, slimServer);
    instance = this;
  }
}
