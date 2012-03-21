// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesseMain;

import fitnesse.Arguments;
import fitnesse.ComponentFactory;
import fitnesse.FitNesse;
import fitnesse.FitNesseContext;
import fitnesse.authentication.Authenticator;
import fitnesse.authentication.MultiUserAuthenticator;
import fitnesse.authentication.OneUserAuthenticator;
import fitnesse.authentication.PromiscuousAuthenticator;
import fitnesse.testutil.FitNesseUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.*;

public class FitNesseMainTest {

  private FitNesseContext context;

  @Before
  public void setUp() throws Exception {
    context = new FitNesseContext();
  }

  @After
  public void tearDown() throws Exception {
    FileUtil.deleteFileSystemDirectory("testFitnesseRoot");
  }

  @Test
  public void testInstallOnly() throws Exception {
    Arguments args = new Arguments();
    args.setInstallOnly(true);
    FitNesse fitnesse = mock(FitNesse.class);
    FitNesseMain.updateAndLaunch(args, context, fitnesse);
    verify(fitnesse, never()).start();
    verify(fitnesse, times(1)).applyUpdates();
  }

  @Test
  public void commandArgCallsExecuteSingleCommand() throws Exception {
    FitNesseMain.dontExitAfterSingleCommand = true;
    Arguments args = new Arguments();
    args.setCommand("command");
    FitNesse fitnesse = mock(FitNesse.class);
    when(fitnesse.start()).thenReturn(true);
    FitNesseMain.updateAndLaunch(args, context, fitnesse);
    verify(fitnesse, times(1)).applyUpdates();
    verify(fitnesse, times(1)).start();
    verify(fitnesse, times(1)).executeSingleCommand("command", System.out);
    verify(fitnesse, times(1)).stop();
  }

  @Test
  public void testDirCreations() throws Exception {
    context.port = 80;
    context.rootPagePath = "testFitnesseRoot";
    new FitNesse(context);

    assertTrue(new File("testFitnesseRoot").exists());
    assertTrue(new File("testFitnesseRoot/files").exists());
  }

  @Test
  public void testMakeNullAuthenticator() throws Exception {
    Authenticator a = FitNesseMain.makeAuthenticator(null,
      new ComponentFactory("blah"));
    assertTrue(a instanceof PromiscuousAuthenticator);
  }

  @Test
  public void testMakeOneUserAuthenticator() throws Exception {
    Authenticator a = FitNesseMain.makeAuthenticator("bob:uncle",
      new ComponentFactory("blah"));
    assertTrue(a instanceof OneUserAuthenticator);
    OneUserAuthenticator oua = (OneUserAuthenticator) a;
    assertEquals("bob", oua.getUser());
    assertEquals("uncle", oua.getPassword());
  }

  @Test
  public void testMakeMultiUserAuthenticator() throws Exception {
    final String passwordFilename = "testpasswd";
    File passwd = new File(passwordFilename);
    passwd.createNewFile();
    Authenticator a = FitNesseMain.makeAuthenticator(passwordFilename,
      new ComponentFactory("blah"));
    assertTrue(a instanceof MultiUserAuthenticator);
    passwd.delete();
  }

  @Test
  public void testContextFitNesseGetSet() throws Exception {
    FitNesse fitnesse = new FitNesse(context, false);
    assertSame(fitnesse, context.fitnesse);
  }

  @Test
  public void testIsRunning() throws Exception {
    context.port = FitNesseUtil.port;
    FitNesse fitnesse = new FitNesse(context, false);

    assertFalse(fitnesse.isRunning());

    fitnesse.start();
    assertTrue(fitnesse.isRunning());

    fitnesse.stop();
    assertFalse(fitnesse.isRunning());
  }

  @Test
  public void testShouldInitializeFitNesseContext() {
    context.port = FitNesseUtil.port;
    new FitNesse(context, false);
    assertNotNull(FitNesseContext.globalContext);
  }

  @Test
  public void canRunSingleCommand() throws Exception {
    String response = runFitnesseMainWith("-o", "-c", "/root");
    assertThat(response, containsString("Command Output"));
  }

  @Test
  public void canRunSingleCommandWithAuthentication() throws Exception {
    String response = runFitnesseMainWith("-o", "-a", "user:pwd", "-c", "user:pwd:/FitNesse.ReadProtectedPage");
    assertThat(response, containsString("HTTP/1.1 200 OK"));
  }

  @Test
  public void testGetHelp() throws Exception {
    String[] messages = arrayOfExpectedHelpLines();
    String helpMessage = helpMessage();

    assertEquals("should be the right number of lines", messages.length, helpMessage.split("\n").length);

    for (String message : messages) {
      assertTrue("help message should contain " + message, helpMessage.contains(message));
    }
  }

  private String[] arrayOfExpectedHelpLines() {
    String header = "Usage: java -jar fitnesse.jar [-pdrleoa]";
    String portHeader = "-p <port number> {80}";

    String dirHeader = "-d <working directory> {.}";
    String rootHeader = "-r <page root directory> {FitNesseRoot}";
    String loggingHeader = "-l <log directory> {no logging}";
    String expiryHeader = "-e <days> {14} Number of days before page versions expire";
    String omitUpdatesHeader = "-o omit updates";
    String authHeader = "-a {user:pwd | user-file-name} enable authentication.";
    String installOnlyHeader = "-i Install only, then quit.";
    String commandHeader = "-c <command> execute single command.";
    String verifyHeader = "-y verify syntax and structure of tests only.";

    return new String[]{header, portHeader, dirHeader, rootHeader, loggingHeader, expiryHeader, omitUpdatesHeader, authHeader, installOnlyHeader, commandHeader, verifyHeader};
  }

  private String helpMessage() {
    PrintStream err = System.err;
    ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errBytes));
    FitNesseMain.printUsage();
    System.setErr(err);

    return errBytes.toString();
  }


  private String runFitnesseMainWith(String... args) throws Exception {
    FitNesseMain.dontExitAfterSingleCommand = true;
    PrintStream out = System.out;
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputBytes));
    FitNesseMain.main(args);
    System.setOut(out);
    String response = outputBytes.toString();
    return response;
  }
}
