// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.responders;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeInstance;

import fitnesse.FitNesseContext;
import fitnesse.responders.templateUtilities.HtmlPage;
import fitnesse.responders.templateUtilities.TraverseDirective;

public class PageFactory {
  
  private static VelocityEngine velocityEngine = null;
  private FitNesseContext context;

  public PageFactory(FitNesseContext context) {
    super();
    this.context = context;
  }

  public HtmlPage newPage() {
    
    return new HtmlPage(getVelocityEngine(), "skeleton.vm");
  }

  public String render(VelocityContext context, String templateName) {
    Writer writer = new StringWriter();
    Template template = getVelocityEngine().getTemplate(templateName);
    template.merge(context, writer);
    return writer.toString();
  }
  
  public String toString() {
    return getClass().getName();
  }

  public VelocityEngine getVelocityEngine() {
    if (velocityEngine == null) {
      Properties properties = new Properties();

      String templatePath = getTemplatePath();

      properties.setProperty(VelocityEngine.RESOURCE_LOADER, "file,classpath");
      properties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, templatePath);

//      instance.velocityEngine.setProperty(
//            "file." + VelocityEngine.RESOURCE_LOADER + ".class",
//            FileResourceLoader.class.getName());

      properties.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class",
            org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader.class.getName());

      velocityEngine = new VelocityEngine();
      velocityEngine.init(properties);
      
      // TODO: Add traverse directive
      velocityEngine.loadDirective(TraverseDirective.class.getName());
    }
    return velocityEngine;
  }

  public String getTemplatePath() {
    return String.format("%s/%s/files/templates", context.rootPath, context.rootDirectoryName);
  }

}
